package com.example.fms.flight.dto;

import lombok.Data;

@Data
public class AddInventoryRequest {

    private String airlineId;
    private String flightNumber;

    private String fromPlace;
    private String toPlace;

    private String departureTime;
    private String arrivalTime;

    private double price;
    private int totalSeats;
}
