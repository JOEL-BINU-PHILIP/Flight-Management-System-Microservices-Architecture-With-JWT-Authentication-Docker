package com.example.fms.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDTO {
    private String pnr;
    private String flightId;
    private String email;
    private int seatsBooked;
    private String bookingTime;
    private boolean cancelled;
    private String cancelledAt;
}
