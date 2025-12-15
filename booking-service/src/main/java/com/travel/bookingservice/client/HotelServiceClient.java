package com.travel.bookingservice.client;

import com.travel.bookingservice.dto.ApiResponse;
import com.travel.bookingservice.dto.external.HotelAvailabilityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "hotel-service", url = "${services.hotel}")
public interface HotelServiceClient {

    @GetMapping("/api/hotels/{id}/availability")
    ApiResponse<HotelAvailabilityDTO> checkAvailability(@PathVariable Long id);

    @PostMapping("/api/hotels/{id}/reserve")
    ApiResponse<Boolean> reserveRoom(@PathVariable Long id);
}