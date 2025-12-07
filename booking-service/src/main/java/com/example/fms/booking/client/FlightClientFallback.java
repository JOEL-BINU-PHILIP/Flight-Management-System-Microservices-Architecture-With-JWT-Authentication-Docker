package com.example.fms.booking.client;

import com.example.fms.booking.dto.BookingRequest;
import com.example.fms.booking.dto.FlightInfoDTO;
import com.example.fms.booking.dto.ReserveSeatRequest;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class FlightClientFallback implements FlightClient {

    @Override
    public Boolean reserveSeats(String flightId, ReserveSeatRequest req) {
        // Flight service is down → seat reservation fails safely
        log.error("Flight-Service DOWN → Cannot reserve seats");
        return false;
    }

    @Override
    public FlightInfoDTO getFlightDetails(String id) {
        // Flight service is down → return null OR empty DTO
        log.error("Flight-Service DOWN → Cannot fetch flight details for ID: {}", id);
        return null;
    }
}
