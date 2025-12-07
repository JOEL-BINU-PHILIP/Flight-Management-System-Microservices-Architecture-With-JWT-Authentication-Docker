package com.example.fms.common.dto;

import java.time.Instant;

public record BookingDto(
        String pnr,
        String flightId,
        String passengerName,
        String passengerEmail,
        int seatsBooked,
        double totalAmount,
        Instant bookingTime,
        String status
) {
}
