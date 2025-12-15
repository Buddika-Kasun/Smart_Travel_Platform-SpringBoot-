package com.travel.bookingservice.client;

import com.travel.bookingservice.dto.ApiResponse;
import com.travel.bookingservice.dto.external.FlightAvailabilityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "flight-service", url = "${services.flight}")
public interface FlightServiceClient {

    @GetMapping("/api/flights/{id}/availability")
    ApiResponse<FlightAvailabilityDTO> checkAvailability(@PathVariable Long id);

    @PostMapping("/api/flights/{id}/reserve")
    ApiResponse<Boolean> reserveSeat(@PathVariable Long id);
}