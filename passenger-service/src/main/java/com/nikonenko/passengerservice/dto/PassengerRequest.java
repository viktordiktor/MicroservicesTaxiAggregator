package com.nikonenko.passengerservice.dto;

import com.nikonenko.passengerservice.utils.ErrorList;
import com.nikonenko.passengerservice.utils.PatternList;
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
    @NotBlank(message = ErrorList.USERNAME_REQUIRED)
    @Size(max = 20, message = ErrorList.WRONG_MAX_USERNAME_SIZE)
    private String username;
    @NotBlank(message = ErrorList.PHONE_REQUIRED)
    @Pattern(regexp = PatternList.PHONE_PATTERN, message = ErrorList.WRONG_PHONE_FORMAT)
    private String phone;
}
