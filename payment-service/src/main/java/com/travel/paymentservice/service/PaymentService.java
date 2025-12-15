package com.travel.paymentservice.service;

import com.travel.paymentservice.dto.PaymentRequestDTO;
import com.travel.paymentservice.dto.PaymentResponseDTO;
import com.travel.paymentservice.entity.Payment;
import com.travel.paymentservice.entity.PaymentStatus;
import com.travel.paymentservice.exception.PaymentException;
import com.travel.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient webClient;

    @Value("${services.booking}")
    private String bookingServiceUrl;

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing payment for booking: {}", request.getBookingId());

        // Create payment record
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Simulate payment processing
            log.info("Simulating payment gateway processing...");
            Thread.sleep(2000); // Simulate payment processing delay

            // Simulate 90% success rate
            boolean paymentSuccess = Math.random() < 0.9;

            if (paymentSuccess) {
                // Update payment status
                savedPayment.setStatus(PaymentStatus.SUCCESS);
                savedPayment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                paymentRepository.save(savedPayment);

                // Update booking status to CONFIRMED via WebClient
                updateBookingStatus(request.getBookingId(), "CONFIRMED");

                log.info("Payment successful for booking: {}", request.getBookingId());
                return mapToDTO(savedPayment, "Payment processed successfully");
            } else {
                // Payment failed
                savedPayment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(savedPayment);

                // Update booking status to FAILED
                updateBookingStatus(request.getBookingId(), "FAILED");

                throw new PaymentException("Payment processing failed");
            }
        } catch (InterruptedException e) {
            log.error("Payment processing interrupted", e);
            savedPayment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(savedPayment);
            throw new PaymentException("Payment processing error");
        }
    }

    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + id));
        return mapToDTO(payment, "Payment retrieved successfully");
    }

    public PaymentResponseDTO getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentException("Payment not found for booking: " + bookingId));
        return mapToDTO(payment, "Payment retrieved successfully");
    }

    private void updateBookingStatus(Long bookingId, String status) {
        try {
            log.info("Updating booking {} status to {}", bookingId, status);

            webClient.put()
                    .uri(bookingServiceUrl + "/api/bookings/" + bookingId + "/status?status=" + status)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Booking status updated successfully");
        } catch (Exception e) {
            log.error("Failed to update booking status", e);
        }
    }

    private PaymentResponseDTO mapToDTO(Payment payment, String message) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getCreatedAt(),
                message
        );
    }
}