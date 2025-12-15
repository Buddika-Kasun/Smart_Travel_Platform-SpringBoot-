package com.travel.paymentservice.controller;

import com.travel.paymentservice.dto.ApiResponse;
import com.travel.paymentservice.dto.PaymentRequestDTO;
import com.travel.paymentservice.dto.PaymentResponseDTO;
import com.travel.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Service", description = "Payment processing APIs")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process payment")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO payment = paymentService.processPayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment processed", payment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved", payment));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payment by booking ID")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentByBookingId(
            @PathVariable Long bookingId) {
        PaymentResponseDTO payment = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved", payment));
    }
}