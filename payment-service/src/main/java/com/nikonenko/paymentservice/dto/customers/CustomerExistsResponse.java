package com.nikonenko.paymentservice.dto.customers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerExistsResponse {
    private boolean isExists;
}
