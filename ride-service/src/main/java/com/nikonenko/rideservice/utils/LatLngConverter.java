package com.nikonenko.rideservice.utils;

import org.springframework.core.convert.converter.Converter;
import com.google.maps.model.LatLng;

public class LatLngConverter implements Converter<String, LatLng> {
    @Override
    public LatLng convert(String source) {
        String[] coordinates = source.split(",");
        double lat = Double.parseDouble(coordinates[0].trim());
        double lng = Double.parseDouble(coordinates[1].trim());
        return new LatLng(lat, lng);
    }
}
