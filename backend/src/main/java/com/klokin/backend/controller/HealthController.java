package com.klokin.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Proves the API is running; later the mobile app pings this before it syncs
@RestController
@RequestMapping("/api/health")
public class HealthController {

    // GET /api/health returns {"status":"UP","app":"Klokin API"}
    @GetMapping
    public Map<String, String> health() {
        // Map.of builds a small read-only map; Spring turns it into JSON automatically
        return Map.of("status", "UP", "app", "Klokin API");
    }
}
