package com.sonoou.alphagym.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.sonoou.alphagym.dto.*;
import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.entity.PaymentTransactionEntity;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import com.sonoou.alphagym.repository.PaymentTransactionRepository;
import com.sonoou.alphagym.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RazorpayService {

    @Value("${app.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${app.razorpay.currency:INR}")
    private String defaultCurrency;

    private final MembershipPlanRepository planRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository transactionRepository;

    public RazorpayService(MembershipPlanRepository planRepository,
                           UserRepository userRepository,
                           PaymentTransactionRepository transactionRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public RazorpayOrderResponse createOrder(String userEmail, CreateOrderRequest request) {
        try {
            // Validate startDate if provided
            if (request.getStartDate() != null) {
                LocalDate today = LocalDate.now();
                if (request.getStartDate().isBefore(today)) {
                    throw new IllegalArgumentException("Membership start date cannot be in the past. It must be today (" + today + ") or a future date.");
                }
            }

            Double amount = request.getAmount();
            String currency = request.getCurrency() != null ? request.getCurrency() : defaultCurrency;
            String receipt = request.getReceipt();
            String planName = "Gym Membership";

            if (request.getPlanId() != null) {
                MembershipPlanEntity plan = planRepository.findById(request.getPlanId())
                        .orElseThrow(() -> new IllegalArgumentException("Membership Plan not found with ID: " + request.getPlanId()));
                amount = plan.getAmount();
                planName = plan.getName();
                if (plan.getCurrency() != null) {
                    currency = plan.getCurrency();
                }
                receipt = "plan_" + plan.getId() + "_" + UUID.randomUUID().toString().substring(0, 6);
            }

            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("Invalid payment amount");
            }

            if (receipt == null) {
                receipt = "rcpt_" + UUID.randomUUID().toString().substring(0, 8);
            }

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            int amountInPaisa = (int) Math.round(amount * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaisa);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");
            String status = order.get("status");

            // Record transaction in DB if user logged in
            if (userEmail != null) {
                final Double finalTxAmount = amount;
                final String finalTxCurrency = currency;
                final String finalPlanName = planName;
                userRepository.findByEmail(userEmail).ifPresent(user -> {
                    PaymentTransactionEntity tx = new PaymentTransactionEntity();
                    tx.setUser(user);
                    tx.setRazorpayOrderId(orderId);
                    tx.setAmount(finalTxAmount);
                    tx.setCurrency(finalTxCurrency);
                    tx.setPlanId(request.getPlanId());
                    tx.setPlanName(finalPlanName);
                    tx.setStatus("CREATED");
                    transactionRepository.save(tx);
                });
            }

            return new RazorpayOrderResponse(orderId, razorpayKeyId, amountInPaisa, currency, status);
        } catch (Exception e) {
            throw new RuntimeException("Razorpay Order Creation Failed: " + e.getMessage(), e);
        }
    }

    public PaymentVerificationResponse verifyPayment(String userEmail, PaymentVerificationRequest request) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate start = request.getStartDate() != null ? request.getStartDate() : today;

            if (start.isBefore(today)) {
                return new PaymentVerificationResponse("FAILED", "Membership start date cannot be in the past. It must be today (" + today + ") or a future date.", request.getRazorpayPaymentId());
            }

            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (isValidSignature) {
                if (userEmail != null) {
                    userRepository.findByEmail(userEmail).ifPresent(user -> {
                        String planName = "Gym Membership";
                        int durationMonths = 1;

                        if (request.getPlanId() != null) {
                            MembershipPlanEntity plan = planRepository.findById(request.getPlanId()).orElse(null);
                            if (plan != null) {
                                planName = plan.getName();
                                durationMonths = plan.getDurationMonths() != null ? plan.getDurationMonths() : 1;
                            }
                        }

                        LocalDateTime startDateTime = start.atStartOfDay();
                        LocalDateTime expiryDateTime = start.plusMonths(durationMonths).atTime(23, 59, 59);

                        user.setActivePlanName(planName);
                        user.setPlanStartDate(startDateTime);
                        user.setPlanExpiryDate(expiryDateTime);
                        user.setMembershipActive(true);
                        userRepository.save(user);

                        // Save or Update Payment Transaction record
                        String finalPlanName = planName;
                        PaymentTransactionEntity tx = transactionRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                                .orElseGet(() -> {
                                    PaymentTransactionEntity newTx = new PaymentTransactionEntity();
                                    newTx.setUser(user);
                                    newTx.setRazorpayOrderId(request.getRazorpayOrderId());
                                    return newTx;
                                });

                        tx.setRazorpayPaymentId(request.getRazorpayPaymentId());
                        tx.setRazorpaySignature(request.getRazorpaySignature());
                        tx.setPlanId(request.getPlanId());
                        tx.setPlanName(finalPlanName);
                        tx.setStatus("SUCCESS");
                        transactionRepository.save(tx);
                    });
                }

                return new PaymentVerificationResponse("SUCCESS", "Payment verified & membership activated successfully starting on " + start.toString(), request.getRazorpayPaymentId());
            } else {
                // Update transaction as failed
                transactionRepository.findByRazorpayOrderId(request.getRazorpayOrderId()).ifPresent(tx -> {
                    tx.setStatus("FAILED");
                    tx.setRazorpayPaymentId(request.getRazorpayPaymentId());
                    transactionRepository.save(tx);
                });
                return new PaymentVerificationResponse("FAILED", "Invalid payment signature verification", request.getRazorpayPaymentId());
            }
        } catch (Exception e) {
            return new PaymentVerificationResponse("FAILED", "Payment verification error: " + e.getMessage(), request.getRazorpayPaymentId());
        }
    }

    public List<PaymentHistoryResponse> getPaymentHistory(String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        List<PaymentTransactionEntity> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return transactions.stream().map(tx -> new PaymentHistoryResponse(
                tx.getId(),
                tx.getRazorpayOrderId(),
                tx.getRazorpayPaymentId(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getPlanId(),
                tx.getPlanName(),
                tx.getStatus(),
                tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : null,
                "/api/payment/receipt/" + tx.getId() + "/download"
        )).collect(Collectors.toList());
    }
}
