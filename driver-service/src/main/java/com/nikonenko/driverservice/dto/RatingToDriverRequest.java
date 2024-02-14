package com.nikonenko.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingToDriverRequest {
    private Long driverId;
    private int rating;
    private String comment;
}
