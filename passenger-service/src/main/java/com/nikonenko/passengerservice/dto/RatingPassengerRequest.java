package com.nikonenko.passengerservice.dto;

import com.nikonenko.passengerservice.utils.ErrorList;
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
public class RatingPassengerRequest {
    @Min(value = 1, message = ErrorList.MIN_RATING_WRONG)
    @Max(value = 5, message = ErrorList.MAX_RATING_WRONG)
    private int rating;
    private String comment;
}
