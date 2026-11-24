package com.klokin.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

// Gives the app one shared "now". Code asks the Clock for the time instead of calling Instant.now()
// directly, so tests can hand in a fixed clock and always get the same answer.
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
