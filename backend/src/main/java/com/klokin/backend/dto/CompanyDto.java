package com.klokin.backend.dto;

import com.klokin.backend.domain.Company;
import com.klokin.backend.factory.CompanyFactory;

import java.time.LocalTime;

// The JSON shapes the API accepts and returns for companies. Keeping these separate from the
// entity means we choose exactly what goes over the wire, and the database model can change freely.
public final class CompanyDto {

    private CompanyDto() {
    }

    // What the client sends. Records give us constructor, getters, equals and toString for free.
    public record Request(String name,
                          String timeZone,
                          LocalTime weekdayStart,
                          LocalTime weekdayEnd,
                          LocalTime saturdayStart,
                          LocalTime saturdayEnd,
                          Integer lunchMinutes) {

        // Hands the raw input to the factory, which validates it
        public Company toCompany() {
            return CompanyFactory.create(name, timeZone, weekdayStart, weekdayEnd,
                    saturdayStart, saturdayEnd, lunchMinutes);
        }
    }

    // What the API sends back
    public record Response(Long id,
                           String name,
                           String timeZone,
                           LocalTime weekdayStart,
                           LocalTime weekdayEnd,
                           LocalTime saturdayStart,
                           LocalTime saturdayEnd,
                           int lunchMinutes) {

        public static Response from(Company company) {
            return new Response(company.getId(), company.getName(), company.getTimeZone(),
                    company.getWeekdayStart(), company.getWeekdayEnd(),
                    company.getSaturdayStart(), company.getSaturdayEnd(),
                    company.getLunchMinutes());
        }
    }
}
