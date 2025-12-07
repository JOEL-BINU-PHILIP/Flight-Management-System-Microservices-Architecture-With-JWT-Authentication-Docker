package com.example.fms.flight.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "flights")
public class Flight {

    @Id
    private String id;

    private String flightNumber;

    private String fromPlace;
    private String toPlace;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private double price;
    private int totalSeats;
    private int availableSeats;

    private String airlineId;  // FK to Airline
}
