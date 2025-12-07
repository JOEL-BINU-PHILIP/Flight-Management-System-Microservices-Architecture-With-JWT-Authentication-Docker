package com.example.fms.common.dto;

public record FlightDto(
        String id,
        String airline,
        String flightNumber,
        String origin,
        String destination,
        int totalSeats,
        int availableSeats,
        double price
) {
}
