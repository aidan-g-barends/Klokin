package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.WorkLocation;
import com.klokin.backend.util.Helper;

public final class WorkLocationFactory {

    public static final int DEFAULT_RADIUS_METRES = 100;
    // Under 30 m GPS jitter causes false clock-outs; over 1 km is no longer "at the shop"
    private static final int MIN_RADIUS_METRES = 30;
    private static final int MAX_RADIUS_METRES = 1000;

    private WorkLocationFactory() {
    }

    public static WorkLocation create(Company company, String name, Double latitude, Double longitude,
                                      Integer radiusMetres, String wifiSsid) {
        Helper.requireNotNull(company, "Company");
        Helper.requireText(name, "Location name");
        if (!Helper.isValidLatitude(latitude)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (!Helper.isValidLongitude(longitude)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }

        int radius = radiusMetres != null ? radiusMetres : DEFAULT_RADIUS_METRES;
        if (radius < MIN_RADIUS_METRES || radius > MAX_RADIUS_METRES) {
            throw new IllegalArgumentException(
                    "Radius must be between " + MIN_RADIUS_METRES + " and " + MAX_RADIUS_METRES + " metres");
        }

        return new WorkLocation.Builder()
                .setCompany(company)
                .setName(name.trim())
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setRadiusMetres(radius)
                .setWifiSsid(Helper.isNullOrEmpty(wifiSsid) ? null : wifiSsid.trim())
                .build();
    }
}
