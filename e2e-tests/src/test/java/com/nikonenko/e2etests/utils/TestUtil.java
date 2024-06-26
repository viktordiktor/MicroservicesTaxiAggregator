package com.nikonenko.e2etests.utils;

import com.google.maps.model.LatLng;
import com.nikonenko.e2etests.dto.CalculateDistanceRequest;
import com.nikonenko.e2etests.dto.CarResponse;
import com.nikonenko.e2etests.dto.ChangeRideStatusRequest;
import com.nikonenko.e2etests.dto.CreateRideRequest;
import com.nikonenko.e2etests.dto.CustomerCalculateRideRequest;
import com.nikonenko.e2etests.dto.CustomerChargeRequest;
import com.nikonenko.e2etests.dto.CustomerCreationRequest;
import com.nikonenko.e2etests.dto.ReviewRequest;
import com.nikonenko.e2etests.models.RideAction;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestUtil {
    public CalculateDistanceRequest createCalculateDistanceRequest(String startGeoString, String endGeoString) {
        return new CalculateDistanceRequest(convert(startGeoString), convert(endGeoString));
    }

    public LatLng convert(String source) {
        String[] coordinates = source.split(",");
        double lat = Double.parseDouble(coordinates[0].trim());
        double lng = Double.parseDouble(coordinates[1].trim());
        return new LatLng(lat, lng);
    }

    public CustomerCalculateRideRequest getCalculateRideRequest(Double rideLength,
                                                                       String rideDateTime, String coupon) {
        return CustomerCalculateRideRequest.builder()
                .rideLength(rideLength)
                .rideDateTime(LocalDateTime.parse(rideDateTime))
                .coupon(coupon)
                .build();
    }

    public CustomerCreationRequest createCustomerCreationRequest(String username, String phone,
                                                                 UUID passengerId, String amount) {
        return CustomerCreationRequest.builder()
                .username(username)
                .phone(phone)
                .passengerId(passengerId)
                .amount(new BigDecimal(amount))
                .build();
    }

    public CustomerChargeRequest getCustomerChargeRequest(UUID passengerId, String currency, BigDecimal price) {
        return CustomerChargeRequest.builder()
                .passengerId(passengerId)
                .currency(currency)
                .amount(price)
                .build();
    }

    public CreateRideRequest getCreateRideRequestByCard(String chargeId, Double distance,
                                                  String startAddress, String endAddress, UUID passengerId) {
        return CreateRideRequest.builder()
                .chargeId(chargeId)
                .distance(distance)
                .startAddress(startAddress)
                .endAddress(endAddress)
                .passengerId(passengerId)
                .build();
    }

    public CreateRideRequest getCreateRideRequestByCash(Double distance, String startAddress,
                                                        String endAddress, UUID passengerId) {
        return CreateRideRequest.builder()
                .distance(distance)
                .startAddress(startAddress)
                .endAddress(endAddress)
                .passengerId(passengerId)
                .build();
    }

    public ChangeRideStatusRequest getChangeRideStatusRequest(String rideId, String action, UUID driverId) {
        return ChangeRideStatusRequest.builder()
                .car(new CarResponse())
                .rideId(rideId)
                .rideAction(RideAction.valueOf(action))
                .driverId(driverId)
                .build();
    }

    public ReviewRequest getReviewRequest(String id, Integer rating, String comment) {
        return ReviewRequest.builder()
                .rideId(id)
                .rating(rating)
                .comment(comment)
                .build();
    }
}
