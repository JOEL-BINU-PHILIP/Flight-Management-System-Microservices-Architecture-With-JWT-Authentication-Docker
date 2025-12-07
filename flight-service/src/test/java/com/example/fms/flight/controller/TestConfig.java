package com.example.fms.flight.controller;

import com.example.fms.flight.service.FlightService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public FlightService flightService() {
        return Mockito.mock(FlightService.class);
    }
}
