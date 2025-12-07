package com.example.fms.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightInfoDTO {
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
