package com.example.fms.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TicketResponse {

    private BookingDTO booking;
    private List<PassengerDTO> passengers;
    private FlightInfoDTO flight;   // from flight-service
}
