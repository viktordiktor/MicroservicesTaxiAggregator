package com.nikonenko.rideservice.utils;

import com.nikonenko.rideservice.exceptions.WrongLatLngParameterException;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import com.google.maps.model.LatLng;

@Configuration
public class LatLngConverter implements Converter<String, LatLng> {
    @Override
    public LatLng convert(@NotNull String source) {
        try {
            String[] coordinates = source.split(",");
            double lat = Double.parseDouble(coordinates[0].trim());
            double lng = Double.parseDouble(coordinates[1].trim());
            return new LatLng(lat, lng);
        } catch (Exception ex) {
            throw new WrongLatLngParameterException();
        }

    }
}
