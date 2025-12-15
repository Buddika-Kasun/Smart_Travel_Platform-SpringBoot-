package com.travel.bookingservice.dto.external;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserValidationDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
}