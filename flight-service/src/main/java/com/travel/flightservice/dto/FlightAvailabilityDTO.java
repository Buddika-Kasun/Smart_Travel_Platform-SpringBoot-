package com.travel.flightservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightAvailabilityDTO {
    private Long flightId;
    private String flightNumber;
    private boolean available;
    private Integer availableSeats;
    private BigDecimal price;
    private String message;

    public static FlightAvailabilityDTO available(Long id, String number, Integer seats, BigDecimal price) {
        return new FlightAvailabilityDTO(id, number, true, seats, price, "Flight available");
    }

    public static FlightAvailabilityDTO unavailable(Long id, String number, String message) {
        return new FlightAvailabilityDTO(id, number, false, 0, BigDecimal.ZERO, message);
    }
}