package com.travel.hotelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityDTO {
    private Long hotelId;
    private String hotelName;
    private boolean available;
    private Integer availableRooms;
    private BigDecimal pricePerNight;
    private String message;

    public static HotelAvailabilityDTO available(Long id, String name, Integer rooms, BigDecimal price) {
        return new HotelAvailabilityDTO(id, name, true, rooms, price, "Hotel available");
    }

    public static HotelAvailabilityDTO unavailable(Long id, String name, String message) {
        return new HotelAvailabilityDTO(id, name, false, 0, BigDecimal.ZERO, message);
    }
}