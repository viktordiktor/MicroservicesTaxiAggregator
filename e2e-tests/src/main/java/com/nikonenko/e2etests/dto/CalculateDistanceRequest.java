package com.nikonenko.e2etests.dto;

import com.google.maps.model.LatLng;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateDistanceRequest {
    private LatLng startGeo;
    private LatLng endGeo;
}