package com.travel.bookingservice.dto.external;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotelAvailabilityDTO {
    private Long hotelId;
    private String hotelName;
    private boolean available;
    private Integer availableRooms;
    private BigDecimal pricePerNight;
    private String message;
}