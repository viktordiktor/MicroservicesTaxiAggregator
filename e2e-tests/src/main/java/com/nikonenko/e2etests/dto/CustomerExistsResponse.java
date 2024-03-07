package com.nikonenko.e2etests.dto;

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
    private String errorMessage;
}
