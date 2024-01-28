package com.nikonenko.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 20, message = "Username must not exceed 20 characters")
    private String username;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\+375\\d{9}", message = "Phone must be in the format +375*********")
    private String phone;

    @Pattern(regexp = "\\d{16}", message = "Card should be a 16-digit number!")
    private String creditCard;
}
