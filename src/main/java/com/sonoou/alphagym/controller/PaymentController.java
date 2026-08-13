package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.*;
import com.sonoou.alphagym.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayService razorpayService;

    public PaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(Authentication authentication,
                                                             @Valid @RequestBody CreateOrderRequest request) {
        String email = authentication != null ? authentication.getName() : null;
        RazorpayOrderResponse response = razorpayService.createOrder(email, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(Authentication authentication,
                                                                    @Valid @RequestBody PaymentVerificationRequest request) {
        String email = authentication != null ? authentication.getName() : null;
        PaymentVerificationResponse response = razorpayService.verifyPayment(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentHistoryResponse>> getPaymentHistory(Authentication authentication) {
        String email = authentication.getName();
        List<PaymentHistoryResponse> history = razorpayService.getPaymentHistory(email);
        return ResponseEntity.ok(history);
    }
}
