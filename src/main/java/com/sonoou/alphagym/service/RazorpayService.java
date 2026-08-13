package com.sonoou.alphagym.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.sonoou.alphagym.dto.CreateOrderRequest;
import com.sonoou.alphagym.dto.PaymentVerificationRequest;
import com.sonoou.alphagym.dto.PaymentVerificationResponse;
import com.sonoou.alphagym.dto.RazorpayOrderResponse;
import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public RazorpayService(MembershipPlanRepository planRepository) {
        this.planRepository = planRepository;
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

    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (isValidSignature) {
                return new PaymentVerificationResponse("SUCCESS", "Payment verified successfully", request.getRazorpayPaymentId());
            } else {
                return new PaymentVerificationResponse("FAILED", "Invalid payment signature verification", request.getRazorpayPaymentId());
            }
        } catch (Exception e) {
            return new PaymentVerificationResponse("FAILED", "Payment verification error: " + e.getMessage(), request.getRazorpayPaymentId());
        }
    }
}
