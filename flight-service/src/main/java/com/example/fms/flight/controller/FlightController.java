package com.example.fms.flight.controller;

import com.example.fms.flight.dto.*;
import com.example.fms.flight.service.FlightService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flight")
public class FlightController {

    private final FlightService flightService;

    // ADMIN → Add Airline
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/airline")
    public ResponseEntity<String> addAirline(@RequestBody AddAirlineRequest req) {
        flightService.addAirline(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Airline created successfully");
    }

    // ADMIN → Add Inventory
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/airline/{airlineId}/inventory")
    public ResponseEntity<String> addInventory(
            @PathVariable("airlineId") String airlineId,
            @RequestBody AddInventoryRequest req) {

        req.setAirlineId(airlineId);
        flightService.addInventory(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Flight inventory added successfully");
    }

    // USER → Search Flights
    @PostMapping("/search")
    public ResponseEntity<SearchFlightResponse> searchFlights(@RequestBody SearchFlightRequest req) {
        return ResponseEntity.ok(flightService.searchFlights(req));
    }

    // USER → Get Flight Details
    @GetMapping("/details/{flightId}")
    public ResponseEntity<FlightDTO> getFlightDetails(@PathVariable("flightId") String flightId) {
        return ResponseEntity.ok(flightService.getFlightDetails(flightId));
    }

    // USER → Reserve Seats (Used By Booking Service)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/reserve/{flightId}")
    public ResponseEntity<Boolean> reserveSeats(
            @PathVariable("flightId") String flightId,
            @RequestBody ReserveSeatRequest req
    ) {
        boolean result = flightService.reserveSeats(flightId, req.getSeats());
        return ResponseEntity.ok(result);
    }
}
