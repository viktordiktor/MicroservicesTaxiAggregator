package com.nikonenko.driverservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDriverRequest {

    @Min(value = 1, message = "Value must be at least 1")
    @Max(value = 5, message = "Value must be at most 5")
    private int rating;

    private String comment;
}
