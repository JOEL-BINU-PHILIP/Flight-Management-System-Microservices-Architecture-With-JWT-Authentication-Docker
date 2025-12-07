package com.example.fms.booking.repository;

import com.example.fms.booking.model.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    List<Passenger> findByBookingId(String bookingId);
}

