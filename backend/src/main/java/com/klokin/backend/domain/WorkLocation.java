package com.klokin.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// A fixed place where staff work, like the shop. The phone auto-clocks in when it's inside the
// circle (latitude/longitude + radius) or sees the shop's Wi-Fi.
@Entity
@Table(name = "work_locations")
public class WorkLocation {

    private static final double EARTH_RADIUS_METRES = 6_371_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private int radiusMetres;

    // Optional Wi-Fi network name; seeing it counts as "at work" even when GPS is weak indoors
    private String wifiSsid;

    protected WorkLocation() {
    }

    private WorkLocation(Builder builder) {
        this.id = builder.id;
        this.company = builder.company;
        this.name = builder.name;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.radiusMetres = builder.radiusMetres;
        this.wifiSsid = builder.wifiSsid;
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRadiusMetres() {
        return radiusMetres;
    }

    public String getWifiSsid() {
        return wifiSsid;
    }

    // Straight-line distance across the Earth's surface (the haversine formula)
    public double distanceMetresTo(double otherLatitude, double otherLongitude) {
        double dLat = Math.toRadians(otherLatitude - latitude);
        double dLng = Math.toRadians(otherLongitude - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(otherLatitude))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_METRES * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // Is this point inside the location's circle?
    public boolean contains(double otherLatitude, double otherLongitude) {
        return distanceMetresTo(otherLatitude, otherLongitude) <= radiusMetres;
    }

    @Override
    public String toString() {
        return "WorkLocation{id=" + id + ", name='" + name + "', radiusMetres=" + radiusMetres + "}";
    }

    public static class Builder {
        private Long id;
        private Company company;
        private String name;
        private double latitude;
        private double longitude;
        private int radiusMetres;
        private String wifiSsid;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCompany(Company company) {
            this.company = company;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setRadiusMetres(int radiusMetres) {
            this.radiusMetres = radiusMetres;
            return this;
        }

        public Builder setWifiSsid(String wifiSsid) {
            this.wifiSsid = wifiSsid;
            return this;
        }

        public Builder copy(WorkLocation location) {
            this.id = location.id;
            this.company = location.company;
            this.name = location.name;
            this.latitude = location.latitude;
            this.longitude = location.longitude;
            this.radiusMetres = location.radiusMetres;
            this.wifiSsid = location.wifiSsid;
            return this;
        }

        public WorkLocation build() {
            return new WorkLocation(this);
        }
    }
}
