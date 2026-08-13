package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.CreateOrderRequest;
import com.sonoou.alphagym.dto.PaymentVerificationRequest;
import com.sonoou.alphagym.dto.PaymentVerificationResponse;
import com.sonoou.alphagym.dto.RazorpayOrderResponse;
import com.sonoou.alphagym.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayService razorpayService;

    public PaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        RazorpayOrderResponse response = razorpayService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        PaymentVerificationResponse response = razorpayService.verifyPayment(request);
        return ResponseEntity.ok(response);
    }
}
