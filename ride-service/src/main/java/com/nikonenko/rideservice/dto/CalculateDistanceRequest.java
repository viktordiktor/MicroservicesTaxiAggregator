package com.nikonenko.rideservice.dto;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.utils.ValidationList;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateDistanceRequest {
    @NotNull(message = ValidationList.START_GEO_REQUIRED)
    private LatLng startGeo;
    @NotNull(message = ValidationList.END_GEO_REQUIRED)
    private LatLng endGeo;
}
