package com.example.fms.flight.controller;

import com.example.fms.flight.dto.FlightDTO;
import com.example.fms.flight.dto.FlightInfoDTO;
import com.example.fms.flight.dto.SearchFlightResponse;
import com.example.fms.flight.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-slice test. TestPropertySource disables Config / Eureka so
 * the test won't attempt to reach external services during bootstrap.
 */
@WebMvcTest(controllers = FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.fail-fast=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void testAddAirline() throws Exception {

        String json = """
            {"name": "IndiGo", "logoUrl": "logo.png"}
            """;

        mockMvc.perform(post("/api/flight/airline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Airline created successfully"));

        verify(flightService, times(1)).addAirline(any());
    }

    @Test
    void testAddInventory() throws Exception {
        String json = """
            {
                "flightNumber":"6E101",
                "fromPlace":"HYD",
                "toPlace":"DEL",
                "departureTime":"2025-01-01T10:00:00",
                "arrivalTime":"2025-01-01T12:00:00",
                "price":4500,
                "totalSeats":100
            }
            """;

        mockMvc.perform(post("/api/flight/airline/A1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Flight inventory added successfully"));

        verify(flightService, times(1)).addInventory(any());
    }

    @Test
    void testSearchFlights() throws Exception {

        SearchFlightResponse response = SearchFlightResponse.builder()
                .flights(List.of(
                        FlightInfoDTO.builder()
                                .flightNumber("6E101")
                                .airlineName("IndiGo")
                                .build()
                ))
                .build();

        when(flightService.searchFlights(any())).thenReturn(response);

        String json = """
            {"fromPlace":"HYD","toPlace":"DEL","date":"2025-01-01"}
            """;

        mockMvc.perform(post("/api/flight/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flights[0].flightNumber").value("6E101"));
    }

    @Test
    void testGetFlightDetails() throws Exception {

        FlightDTO dto = new FlightDTO();
        dto.setFlightNumber("6E101");
        dto.setAirlineName("IndiGo");

        when(flightService.getFlightDetails("F1")).thenReturn(dto);

        mockMvc.perform(get("/api/flight/details/F1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("6E101"));
    }

    @Test
    void testReserveSeats() throws Exception {

        when(flightService.reserveSeats("F1", 2)).thenReturn(true);

        String json = """
            {"seats":2}
            """;

        mockMvc.perform(post("/api/flight/reserve/F1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
