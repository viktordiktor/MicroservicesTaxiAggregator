package com.nikonenko.passengerservice.dto;

import com.google.maps.model.LatLng;
import com.nikonenko.passengerservice.models.feign.RidePaymentMethod;
import com.nikonenko.passengerservice.utils.ValidEnum;
import com.nikonenko.passengerservice.utils.ValidationList;
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
    @NotBlank(message = ValidationList.START_ADDRESS_REQUIRED)
    private LatLng startGeo;
    @NotBlank(message = ValidationList.END_ADDRESS_REQUIRED)
    private LatLng endGeo;
    @NotBlank(message = ValidationList.START_GEO_REQUIRED)
    private String startAddress;
    @NotBlank(message = ValidationList.END_GEO_REQUIRED)
    private String endAddress;
    @NotBlank
    @ValidEnum(value = RidePaymentMethod.class, message = ValidationList.WRONG_ENUM_FORMAT)
    private RidePaymentMethod ridePaymentMethod;
    private String coupon;
    private String currency;
}
