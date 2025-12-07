package com.example.fms.booking.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "passengers")
public class Passenger {

    @Id
    private String id;

    private String name;
    private String gender;
    private int age;
    private String seatNumber;
    private String meal;

    private String bookingId; // FK to Booking
}
