package com.example.fms.booking.service;

import com.example.fms.booking.client.FlightClient;
import com.example.fms.booking.dto.*;
import com.example.fms.booking.model.Booking;
import com.example.fms.booking.model.Passenger;
import com.example.fms.booking.repository.BookingRepository;
import com.example.fms.booking.repository.PassengerRepository;
import com.example.fms.booking.util.PnrGenerator;
import com.example.fms.booking.messaging.EmailProducer;
import com.example.fms.common.dto.EmailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final FlightClient flightClient;
    private final BookingRepository bookingRepo;
    private final PassengerRepository passengerRepo;
    private final EmailProducer emailProducer;
    private final CircuitBreakerFactory cbFactory;

    @Override
    public BookingResponse bookFlight(BookingRequest req) {

        CircuitBreaker breaker = cbFactory.create("flightCB");

        // Prepare seat reservation request
        ReserveSeatRequest seatReq = new ReserveSeatRequest(
                req.getFlightId(),
                req.getPassengers().size()
        );

        Boolean seatReserved = breaker.run(
                () -> flightClient.reserveSeats(req.getFlightId(), seatReq),
                throwable -> false
        );

        if (!seatReserved) {
            throw new RuntimeException("Flight service unavailable / seat reservation failed");
        }

        String pnr = PnrGenerator.generate();

        // Save booking
        Booking booking = new Booking();
        booking.setPnr(pnr);
        booking.setFlightId(req.getFlightId());
        booking.setEmail(req.getEmail());
        booking.setSeatsBooked(req.getPassengers().size());
        booking.setBookingTime(LocalDateTime.now());
        booking.setCanceled(false);

        bookingRepo.save(booking);

        // Save passengers
        for (PassengerDTO p : req.getPassengers()) {
            Passenger px = new Passenger();
            px.setName(p.getName());
            px.setGender(p.getGender());
            px.setAge(p.getAge());
            px.setSeatNumber(p.getSeatNumber());
            px.setMeal(p.getMeal());
            px.setBookingId(booking.getId());
            passengerRepo.save(px);
        }

        // Send RabbitMQ email event
        EmailEvent event = new EmailEvent(
                req.getEmail(),
                "Booking Confirmed",
                "Your flight booking is confirmed.\nPNR: " + pnr,
                "BOOKING_CONFIRMED",
                pnr
        );
        emailProducer.sendEmail(event);

        BookingResponse res = new BookingResponse();
        res.setPnr(pnr);
        res.setEmail(req.getEmail());
        res.setFlightId(req.getFlightId());
        res.setStatus("BOOKED");

        return res;
    }

    @Override
    public CancelResponse cancelBooking(String pnr) {

        Booking booking = bookingRepo.findByPnr(pnr);
        if (booking == null) throw new RuntimeException("Invalid PNR");

        booking.setCanceled(true);
        booking.setCanceledAt(LocalDateTime.now());
        bookingRepo.save(booking);

        // Send cancellation email event
        EmailEvent event = new EmailEvent(
                booking.getEmail(),
                "Booking Cancelled",
                "Your booking has been cancelled.\nPNR: " + pnr,
                "BOOKING_CANCELLED",
                pnr
        );

        emailProducer.sendEmail(event);

        CancelResponse res = new CancelResponse();
        res.setPnr(pnr);
        res.setStatus("CANCELLED");

        return res;
    }

    @Override
    public TicketResponse getTicket(String pnr) {

        Booking booking = bookingRepo.findByPnr(pnr);
        if (booking == null) {
            throw new RuntimeException("Invalid PNR");
        }

        BookingDTO bookingDTO = BookingDTO.builder()
                .pnr(booking.getPnr())
                .flightId(booking.getFlightId())
                .email(booking.getEmail())
                .seatsBooked(booking.getSeatsBooked())
                .bookingTime(String.valueOf(booking.getBookingTime()))
                .cancelled(booking.isCanceled())
                .cancelledAt(booking.getCanceledAt() != null ? booking.getCanceledAt().toString() : null)
                .build();

        List<Passenger> pxList = passengerRepo.findByBookingId(booking.getId());

        List<PassengerDTO> passengerDTOs = pxList.stream().map(p ->
                PassengerDTO.builder()
                        .name(p.getName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .meal(p.getMeal())
                        .build()
        ).toList();

        FlightInfoDTO flightInfo = flightClient.getFlightDetails(booking.getFlightId());

        return TicketResponse.builder()
                .booking(bookingDTO)
                .passengers(passengerDTOs)
                .flight(flightInfo)
                .build();
    }

    @Override
    public Object getHistory(String email) {
        return bookingRepo.findByEmail(email);
    }
}
