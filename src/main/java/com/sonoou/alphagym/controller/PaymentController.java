package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.*;
import com.sonoou.alphagym.service.RazorpayService;
import com.sonoou.alphagym.service.ReceiptPdfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final ReceiptPdfService receiptPdfService;

    public PaymentController(RazorpayService razorpayService, ReceiptPdfService receiptPdfService) {
        this.razorpayService = razorpayService;
        this.receiptPdfService = receiptPdfService;
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

    /**
     * Preview / Stream Payment Receipt PDF in browser
     */
    @GetMapping("/receipt/{transactionId}")
    public ResponseEntity<byte[]> viewReceiptPdf(@PathVariable Long transactionId,
                                                Authentication authentication) {
        String email = authentication.getName();
        byte[] pdfBytes = receiptPdfService.generateReceiptPdf(transactionId, email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "receipt_" + transactionId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Download Payment Receipt PDF as an attachment file
     */
    @GetMapping("/receipt/{transactionId}/download")
    public ResponseEntity<byte[]> downloadReceiptPdf(@PathVariable Long transactionId,
                                                    Authentication authentication) {
        String email = authentication.getName();
        byte[] pdfBytes = receiptPdfService.generateReceiptPdf(transactionId, email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "AlphaVeins_Receipt_" + transactionId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Download Payment Receipt PDF by Razorpay Order ID
     */
    @GetMapping("/receipt/order/{orderId}")
    public ResponseEntity<byte[]> downloadReceiptByOrderId(@PathVariable String orderId,
                                                          Authentication authentication) {
        String email = authentication.getName();
        byte[] pdfBytes = receiptPdfService.generateReceiptPdfByOrderId(orderId, email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "AlphaVeins_Receipt_" + orderId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
