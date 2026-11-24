package com.klokin.backend.util;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.regex.Pattern;

// Small validation helpers the factories share, so each rule lives in one place
public final class Helper {

    // Simple sanity check: something@something.something
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    // South African numbers: 0821234567 or +27821234567 (spaces and dashes removed first)
    private static final Pattern SA_PHONE = Pattern.compile("^(\\+27|0)\\d{9}$");

    // Utility class: only static methods, so nobody should create an instance
    private Helper() {
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isBlank();
    }

    // Throws with a readable message, e.g. "Company name is required"
    public static void requireText(String value, String fieldName) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    // Strips spaces and dashes so "082 123 4567" is accepted and stored as "0821234567"
    public static String normalisePhone(String phone) {
        return phone == null ? null : phone.replaceAll("[\\s-]", "");
    }

    public static boolean isValidSaPhone(String phone) {
        return phone != null && SA_PHONE.matcher(normalisePhone(phone)).matches();
    }

    // True for real IANA zones like "Africa/Johannesburg"
    public static boolean isValidTimeZone(String timeZone) {
        try {
            ZoneId.of(timeZone);
            return true;
        } catch (DateTimeException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90 && latitude <= 90;
    }

    public static boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180 && longitude <= 180;
    }
}
