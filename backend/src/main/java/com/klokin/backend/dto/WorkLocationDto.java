package com.klokin.backend.dto;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.WorkLocation;
import com.klokin.backend.factory.WorkLocationFactory;

public final class WorkLocationDto {

    private WorkLocationDto() {
    }

    public record Request(String name, Double latitude, Double longitude, Integer radiusMetres, String wifiSsid) {

        public WorkLocation toWorkLocation(Company company) {
            return WorkLocationFactory.create(company, name, latitude, longitude, radiusMetres, wifiSsid);
        }
    }

    public record Response(Long id, Long companyId, String name, double latitude, double longitude,
                           int radiusMetres, String wifiSsid) {

        public static Response from(WorkLocation location) {
            return new Response(location.getId(), location.getCompany().getId(), location.getName(),
                    location.getLatitude(), location.getLongitude(),
                    location.getRadiusMetres(), location.getWifiSsid());
        }
    }
}
