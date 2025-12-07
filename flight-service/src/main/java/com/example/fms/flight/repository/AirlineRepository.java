package com.example.fms.flight.repository;

import com.example.fms.flight.model.Airline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirlineRepository extends MongoRepository<Airline, String> {
    boolean existsByNameIgnoreCase(String name);
}
