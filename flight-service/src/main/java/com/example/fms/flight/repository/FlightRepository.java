package com.example.fms.flight.repository;

import com.example.fms.flight.model.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends MongoRepository<Flight, String> {

    // Search flights
    List<Flight> findByFromPlaceAndToPlaceAndDepartureTimeBetween(
            String from,
            String to,
            LocalDateTime start,
            LocalDateTime end
    );
    boolean existsByFlightNumberIgnoreCase(String flightNumber);
}
