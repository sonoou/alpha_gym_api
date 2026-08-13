package com.sonoou.alphagym.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.sonoou.alphagym.dto.CreateOrderRequest;
import com.sonoou.alphagym.dto.PaymentVerificationRequest;
import com.sonoou.alphagym.dto.PaymentVerificationResponse;
import com.sonoou.alphagym.dto.RazorpayOrderResponse;
import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import com.sonoou.alphagym.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public RazorpayService(MembershipPlanRepository planRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    public RazorpayOrderResponse createOrder(CreateOrderRequest request) {
        try {
            Double finalAmount = request.getAmount();
            String currency = request.getCurrency() != null ? request.getCurrency() : defaultCurrency;
            String receipt = request.getReceipt();

            if (request.getPlanId() != null) {
                MembershipPlanEntity plan = planRepository.findById(request.getPlanId())
                        .orElseThrow(() -> new IllegalArgumentException("Membership Plan not found with ID: " + request.getPlanId()));
                finalAmount = plan.getAmount();
                if (plan.getCurrency() != null) {
                    currency = plan.getCurrency();
                }
                receipt = "plan_" + plan.getId() + "_" + UUID.randomUUID().toString().substring(0, 6);
            }

            if (finalAmount == null || finalAmount <= 0) {
                throw new IllegalArgumentException("Invalid payment amount");
            }

            if (receipt == null) {
                receipt = "rcpt_" + UUID.randomUUID().toString().substring(0, 8);
            }

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            int amountInPaisa = (int) Math.round(finalAmount * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaisa);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");
            String status = order.get("status");

            return new RazorpayOrderResponse(orderId, razorpayKeyId, amountInPaisa, currency, status);
        } catch (Exception e) {
            throw new RuntimeException("Razorpay Order Creation Failed: " + e.getMessage(), e);
        }
    }

    public PaymentVerificationResponse verifyPayment(String userEmail, PaymentVerificationRequest request) {
        try {
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

                        user.setActivePlanName(planName);
                        user.setMembershipActive(true);
                        user.setPlanExpiryDate(LocalDateTime.now().plusMonths(durationMonths));
                        userRepository.save(user);
                    });
                }

                return new PaymentVerificationResponse("SUCCESS", "Payment verified & membership activated successfully", request.getRazorpayPaymentId());
            } else {
                return new PaymentVerificationResponse("FAILED", "Invalid payment signature verification", request.getRazorpayPaymentId());
            }
        } catch (Exception e) {
            return new PaymentVerificationResponse("FAILED", "Payment verification error: " + e.getMessage(), request.getRazorpayPaymentId());
        }
    }
}
