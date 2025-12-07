package com.example.fms.booking.service;

import com.example.fms.booking.dto.BookingRequest;
import com.example.fms.booking.dto.BookingResponse;
import com.example.fms.booking.dto.CancelResponse;

public interface BookingService {

    BookingResponse bookFlight(BookingRequest req);

    CancelResponse cancelBooking(String pnr);

    Object getTicket(String pnr);

    Object getHistory(String email);
}
