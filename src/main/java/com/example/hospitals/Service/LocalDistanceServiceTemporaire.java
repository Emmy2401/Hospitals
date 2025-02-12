package com.example.hospitals.Service;

import com.example.hospitals.Interface.DistanceService;

public class LocalDistanceServiceTemporaire implements DistanceService {

    @Override
    public Double calculateDistance(double refLat, double refLng, double targetLat, double targetLng) {
        final int EARTH_RADIUS = 6371; // Rayon de la Terre en kilomètres

        double latDistance = Math.toRadians(targetLat - refLat);
        double lngDistance = Math.toRadians(targetLng - refLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(refLat)) * Math.cos(Math.toRadians(targetLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Distance en kilomètres
    }
}
