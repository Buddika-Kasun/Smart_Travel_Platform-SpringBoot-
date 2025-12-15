package com.travel.bookingservice.service;

import com.travel.bookingservice.client.FlightServiceClient;
import com.travel.bookingservice.client.HotelServiceClient;
import com.travel.bookingservice.dto.ApiResponse;
import com.travel.bookingservice.dto.BookingRequestDTO;
import com.travel.bookingservice.dto.BookingResponseDTO;
import com.travel.bookingservice.dto.external.*;
import com.travel.bookingservice.entity.Booking;
import com.travel.bookingservice.entity.BookingStatus;
import com.travel.bookingservice.exception.BookingException;
import com.travel.bookingservice.exception.ResourceNotFoundException;
import com.travel.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightServiceClient flightServiceClient;
    private final HotelServiceClient hotelServiceClient;
    private final WebClient webClient;

    @Value("${services.user}")
    private String userServiceUrl;

    @Value("${services.notification}")
    private String notificationServiceUrl;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        log.info("Starting booking process for user: {}", request.getUserId());

        // Step 1: Validate user via WebClient
        log.info("Step 1: Validating user");
        boolean isUserValid = validateUser(request.getUserId());
        if (!isUserValid) {
            throw new BookingException("User validation failed");
        }

        // Step 2: Check flight availability via Feign Client
        log.info("Step 2: Checking flight availability");
        ApiResponse<FlightAvailabilityDTO> flightResponse =
                flightServiceClient.checkAvailability(request.getFlightId());

        if (flightResponse.getData() == null || !flightResponse.getData().isAvailable()) {
            throw new BookingException("Flight not available");
        }

        // Step 3: Check hotel availability via Feign Client
        log.info("Step 3: Checking hotel availability");
        ApiResponse<HotelAvailabilityDTO> hotelResponse =
                hotelServiceClient.checkAvailability(request.getHotelId());

        if (hotelResponse.getData() == null || !hotelResponse.getData().isAvailable()) {
            throw new BookingException("Hotel not available");
        }

        // Step 4: Calculate total cost
        log.info("Step 4: Calculating total cost");
        BigDecimal flightPrice = flightResponse.getData().getPrice();
        BigDecimal hotelPrice = hotelResponse.getData().getPricePerNight();
        BigDecimal totalCost = flightPrice.add(hotelPrice);

        // Step 5: Create booking with PENDING status
        log.info("Step 5: Creating booking with PENDING status");
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setFlightId(request.getFlightId());
        booking.setHotelId(request.getHotelId());
        booking.setTravelDate(request.getTravelDate());
        booking.setTotalCost(totalCost);
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingReference("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {} and reference: {}",
                savedBooking.getId(), savedBooking.getBookingReference());

        // Step 6: Reserve flight and hotel
        log.info("Step 6: Reserving flight and hotel");
        try {
            flightServiceClient.reserveSeat(request.getFlightId());
            hotelServiceClient.reserveRoom(request.getHotelId());
        } catch (Exception e) {
            log.error("Failed to reserve flight/hotel", e);
            savedBooking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(savedBooking);
            throw new BookingException("Failed to reserve flight or hotel");
        }

        // Payment will be triggered by Payment Service
        // Notification will be sent after payment confirmation

        return mapToResponseDTO(savedBooking, "Booking created successfully. Proceed to payment.");
    }

    @Transactional
    public BookingResponseDTO updateBookingStatus(Long bookingId, BookingStatus status) {
        log.info("Updating booking {} to status {}", bookingId, status);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(status);
        Booking updated = bookingRepository.save(booking);

        // Send notification via WebClient if confirmed
        if (status == BookingStatus.CONFIRMED) {
            sendNotification(booking);
        }

        return mapToResponseDTO(updated, "Booking status updated to " + status);
    }

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToResponseDTO(booking, "Booking retrieved successfully");
    }

    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(b -> mapToResponseDTO(b, null))
                .collect(Collectors.toList());
    }

    private boolean validateUser(Long userId) {
        try {
            ApiResponse<Boolean> response = webClient.get()
                    .uri(userServiceUrl + "/api/users/validate/" + userId)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<Boolean>>() {})
                    .block();

            return response != null && response.getData() != null && response.getData();
        } catch (Exception e) {
            log.error("Failed to validate user: {}", userId, e);
            return false;
        }
    }

    private void sendNotification(Booking booking) {
        try {
            log.info("Sending notification for booking: {}", booking.getBookingReference());

            NotificationRequest notificationRequest = new NotificationRequest(
                    booking.getUserId(),
                    "Booking Confirmed",
                    String.format("Your booking %s has been confirmed. Total: $%.2f",
                            booking.getBookingReference(), booking.getTotalCost())
            );

            webClient.post()
                    .uri(notificationServiceUrl + "/api/notifications/send")
                    .bodyValue(notificationRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe(
                            result -> log.info("Notification sent successfully"),
                            error -> log.error("Failed to send notification", error)
                    );
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking, String message) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setFlightId(booking.getFlightId());
        dto.setHotelId(booking.getHotelId());
        dto.setTravelDate(booking.getTravelDate());
        dto.setTotalCost(booking.getTotalCost());
        dto.setStatus(booking.getStatus());
        dto.setBookingReference(booking.getBookingReference());
        dto.setMessage(message);
        return dto;
    }
}

// Notification Request DTO
class NotificationRequest {
    private Long userId;
    private String title;
    private String message;

    public NotificationRequest(Long userId, String title, String message) {
        this.userId = userId;
        this.title = title;
        this.message = message;
    }

    // Getters and setters
}