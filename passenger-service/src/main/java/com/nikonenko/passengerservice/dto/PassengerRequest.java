package com.nikonenko.passengerservice.dto;

import com.nikonenko.passengerservice.utils.PatternList;
import com.nikonenko.passengerservice.utils.ValidationList;
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
public class PassengerRequest {
    @NotBlank(message = ValidationList.USERNAME_REQUIRED)
    @Size(max = 20, message = ValidationList.WRONG_MAX_USERNAME_SIZE)
    private String username;
    @NotBlank(message = ValidationList.PHONE_REQUIRED)
    @Pattern(regexp = PatternList.PHONE_PATTERN, message = ValidationList.WRONG_PHONE_FORMAT)
    private String phone;
}
