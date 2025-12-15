package com.travel.flightservice.controller;

import com.travel.flightservice.dto.ApiResponse;
import com.travel.flightservice.dto.FlightAvailabilityDTO;
import com.travel.flightservice.dto.FlightDTO;
import com.travel.flightservice.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flight Service", description = "Flight management APIs")
public class FlightController {
    private final FlightService flightService;

    @PostMapping
    @Operation(summary = "Create a new flight")
    public ResponseEntity<ApiResponse<FlightDTO>> createFlight(@Valid @RequestBody FlightDTO flightDTO) {
        FlightDTO created = flightService.createFlight(flightDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flight created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID")
    public ResponseEntity<ApiResponse<FlightDTO>> getFlightById(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(ApiResponse.success("Flight retrieved successfully", flight));
    }

    @GetMapping
    @Operation(summary = "Get all flights")
    public ResponseEntity<ApiResponse<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(ApiResponse.success("Flights retrieved successfully", flights));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check flight availability")
    public ResponseEntity<ApiResponse<FlightAvailabilityDTO>> checkAvailability(@PathVariable Long id) {
        FlightAvailabilityDTO availability = flightService.checkAvailability(id);
        return ResponseEntity.ok(ApiResponse.success("Availability checked", availability));
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve a seat")
    public ResponseEntity<ApiResponse<Boolean>> reserveSeat(@PathVariable Long id) {
        boolean reserved = flightService.reserveSeat(id);
        return ResponseEntity.ok(ApiResponse.success("Seat reservation result", reserved));
    }
}