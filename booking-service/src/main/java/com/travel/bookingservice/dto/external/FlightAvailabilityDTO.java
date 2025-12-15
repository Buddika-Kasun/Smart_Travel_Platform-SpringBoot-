package com.travel.bookingservice.dto.external;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FlightAvailabilityDTO {
    private Long flightId;
    private String flightNumber;
    private boolean available;
    private Integer availableSeats;
    private BigDecimal price;
    private String message;
}