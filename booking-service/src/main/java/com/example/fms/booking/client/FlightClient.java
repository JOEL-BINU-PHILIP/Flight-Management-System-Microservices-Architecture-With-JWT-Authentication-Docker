package com.example.fms.booking.client;

import com.example.fms.booking.config.FeignClientConfig;
import com.example.fms.booking. dto.FlightInfoDTO;
import com.example.fms.booking.dto.ReserveSeatRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "flight-service",
        path = "/api/flight",
        fallback = FlightClientFallback.class,
        configuration = FeignClientConfig.class  // Add this line
)
public interface FlightClient {

    // Reserve seats in flight-service
    @PostMapping("/reserve/{flightId}")
    Boolean reserveSeats(
            @PathVariable("flightId") String flightId,
            @RequestBody ReserveSeatRequest req
    );

    // Get flight details from flight-service
    @GetMapping("/details/{flightId}")
    FlightInfoDTO getFlightDetails(@PathVariable("flightId") String flightId);
}