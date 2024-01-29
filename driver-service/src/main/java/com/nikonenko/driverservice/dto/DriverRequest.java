package com.nikonenko.driverservice.dto;

import com.nikonenko.driverservice.utils.ErrorList;
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
    @NotBlank(message = ErrorList.USERNAME_REQUIRED)
    @Size(max = 20, message = ErrorList.WRONG_MAX_USERNAME_SIZE)
    private String username;
    @NotBlank(message = ErrorList.PHONE_REQUIRED)
    @Pattern(regexp = "\\+375\\d{9}", message = ErrorList.WRONG_PHONE_FORMAT)
    private String phone;
    @Pattern(regexp = "\\d{16}", message = ErrorList.WRONG_CARD_FORMAT)
    private String creditCard;
}
