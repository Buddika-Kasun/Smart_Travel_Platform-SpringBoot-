package com.travel.hotelservice.controller;

import com.travel.hotelservice.dto.ApiResponse;
import com.travel.hotelservice.dto.HotelAvailabilityDTO;
import com.travel.hotelservice.dto.HotelDTO;
import com.travel.hotelservice.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotel Service", description = "Hotel management APIs")
public class HotelController {
    private final HotelService hotelService;

    @PostMapping
    @Operation(summary = "Create a new hotel")
    public ResponseEntity<ApiResponse<HotelDTO>> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        HotelDTO created = hotelService.createHotel(hotelDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hotel created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<ApiResponse<HotelDTO>> getHotelById(@PathVariable Long id) {
        HotelDTO hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(ApiResponse.success("Hotel retrieved successfully", hotel));
    }

    @GetMapping
    @Operation(summary = "Get all hotels")
    public ResponseEntity<ApiResponse<List<HotelDTO>>> getAllHotels() {
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(ApiResponse.success("Hotels retrieved successfully", hotels));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check hotel availability")
    public ResponseEntity<ApiResponse<HotelAvailabilityDTO>> checkAvailability(@PathVariable Long id) {
        HotelAvailabilityDTO availability = hotelService.checkAvailability(id);
        return ResponseEntity.ok(ApiResponse.success("Availability checked", availability));
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve a room")
    public ResponseEntity<ApiResponse<Boolean>> reserveRoom(@PathVariable Long id) {
        boolean reserved = hotelService.reserveRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room reservation result", reserved));
    }
}