package com.nikonenko.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarRequest {

    @NotBlank(message = "Number is required")
    @Pattern(regexp = "\\d{4}\\s{2}\\d", message = "Number must be in the format 1111A1")
    private String number;

    private String model;

    private String color;
}
