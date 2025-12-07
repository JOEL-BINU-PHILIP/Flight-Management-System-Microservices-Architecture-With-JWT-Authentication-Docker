package com.example.fms.booking.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {

    private String flightId;          // The flight being booked
    private String email;             // User email
    private List<PassengerDTO> passengers;  // Passenger list
}
