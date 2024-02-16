package com.nikonenko.passengerservice.dto.feign.ride;

import com.google.maps.model.LatLng;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateDistanceResponse {
    private LatLng startGeo;
    private LatLng endGeo;
    private Double distance;
}
