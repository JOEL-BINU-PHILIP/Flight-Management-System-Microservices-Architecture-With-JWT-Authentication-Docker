package com.example.fms.booking.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String pnr;
    private String email;
    private String flightId;

    private int seatsBooked;

    private LocalDateTime bookingTime;

    private boolean canceled;
    private LocalDateTime canceledAt;
}
