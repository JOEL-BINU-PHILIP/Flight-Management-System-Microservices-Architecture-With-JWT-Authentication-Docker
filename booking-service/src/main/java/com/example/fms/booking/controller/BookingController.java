package com.example.fms.booking.controller;

import com.example.fms.booking.dto.BookingRequest;
import com.example.fms.booking.dto.BookingResponse;
import com.example.fms.booking.dto.CancelResponse;
import com.example.fms.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@EnableMethodSecurity
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookingController {

    private final BookingService bookingService;

    // USER → Book a flight
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookFlight(@RequestBody BookingRequest req) {
        return ResponseEntity.status(201).body(bookingService.bookFlight(req));
    }

    // USER → Cancel using PNR
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @DeleteMapping("/cancel/{pnr}")
    public ResponseEntity<CancelResponse> cancelBooking(@PathVariable("pnr") String pnr) {
        return ResponseEntity.ok(bookingService.cancelBooking(pnr));
    }

    // USER → Get ticket by PNR
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<?> getTicket(@PathVariable("pnr") String pnr) {
        return ResponseEntity.ok(bookingService.getTicket(pnr));
    }

    // USER → View Booking History by Email
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/history/{email}")
    public ResponseEntity<?> getHistory(@PathVariable("email") String email) {
        return ResponseEntity.ok(bookingService.getHistory(email));
    }
}
