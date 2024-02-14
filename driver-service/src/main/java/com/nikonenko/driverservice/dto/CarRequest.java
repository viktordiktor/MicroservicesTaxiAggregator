package com.nikonenko.driverservice.dto;

import com.nikonenko.driverservice.utils.PatternList;
import com.nikonenko.driverservice.utils.ValidationList;
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
    @NotBlank(message = ValidationList.NUMBER_REQUIRED)
    @Pattern(regexp = PatternList.NUMBER_PATTERN, message = ValidationList.WRONG_NUMBER_FORMAT)
    private String number;
    private String model;
    private String color;
}
