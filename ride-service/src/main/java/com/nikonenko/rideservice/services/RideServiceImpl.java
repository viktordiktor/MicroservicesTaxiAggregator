package com.nikonenko.rideservice.services;

import com.google.maps.model.LatLng;
import com.nikonenko.rideservice.dto.CalculateDistanceRequest;
import com.nikonenko.rideservice.dto.CalculateDistanceResponse;
import com.nikonenko.rideservice.dto.CreateRideRequest;
import com.nikonenko.rideservice.dto.CreateRideResponse;
import com.nikonenko.rideservice.models.Ride;
import com.nikonenko.rideservice.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.referencing.GeodeticCalculator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    @Override
    public CalculateDistanceResponse calculateDistance(CalculateDistanceRequest calculateDistanceRequest) {
        LatLng startGeo = calculateDistanceRequest.getStartGeo();
        LatLng endGeo = calculateDistanceRequest.getEndGeo();
        
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        geoCalc.setStartingGeographicPoint(startGeo.lng, startGeo.lat);
        geoCalc.setDestinationGeographicPoint(endGeo.lng, endGeo.lat);

        double distanceInKilometers = geoCalc.getOrthodromicDistance() / 1000;

        return CalculateDistanceResponse.builder()
                .startGeo(startGeo)
                .endGeo(endGeo)
                .distance(distanceInKilometers)
                .build();
    }

    @Override
    public CreateRideResponse createRide(CreateRideRequest createRideRequest) {
        Ride ride = modelMapper.map(createRideRequest, Ride.class);
        Ride savedRide = rideRepository.save(ride);
        log.info("Created ride with id: {}", savedRide.getId());
        savedRide.setIsActive(true);
        savedRide.setStartDate(LocalDateTime.now());
        return modelMapper.map(savedRide, CreateRideResponse.class);
    }
}
