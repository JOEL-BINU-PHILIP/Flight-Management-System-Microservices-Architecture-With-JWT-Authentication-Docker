package com. example.fms.flight.service;

import com.example.fms.flight.dto.*;
import com.example.fms.flight.model.Airline;
import java.util.List;

public interface FlightService {

    void addAirline(AddAirlineRequest request);

    void addInventory(AddInventoryRequest request);

    SearchFlightResponse searchFlights(SearchFlightRequest request);

    boolean reserveSeats(String flightId, int seats);

    FlightDTO getFlightDetails(String flightId);

    // NEW METHOD - Get all airlines
    List<Airline> getAllAirlines();
}