package com.nikonenko.passengerservice.dto.feign.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreationResponse {
    private String id;
    private String username;
    private String phone;
    private String errorMessage;
}
