package com.example.fms.flight.dto;

import lombok.Data;

@Data
public class FlightDTO {
    private String id;
    private String flightNumber;
    private String fromPlace;
    private String toPlace;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private int availableSeats;

    private String airlineName;
}
