package com.example.fms.booking.repository;

import com.example.fms.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    Booking findByPnr(String pnr);
    List<Booking> findByEmail(String email);
}

