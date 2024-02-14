package com.nikonenko.passengerservice.dto;

import com.google.maps.model.LatLng;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideByPassengerRequest {
    @NotBlank
    private LatLng startGeo;
    @NotBlank
    private LatLng endGeo;
    @NotBlank
    private String startAddress;
    @NotBlank
    private String endAddress;
    @NotBlank
    private RidePaymentMethod ridePaymentMethod;
    private String coupon;
    private String currency;
    //TODO validation!!!
}
