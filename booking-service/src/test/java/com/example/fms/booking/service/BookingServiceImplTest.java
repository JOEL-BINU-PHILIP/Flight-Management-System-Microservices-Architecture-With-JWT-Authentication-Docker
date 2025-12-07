package com.example.fms.booking.service;

import com.example.fms.booking.client.FlightClient;
import com.example.fms.booking.dto.*;
import com.example.fms.booking.messaging.EmailProducer;
import com.example.fms.booking.model.Booking;
import com.example.fms.booking.model.Passenger;
import com.example.fms.booking.repository.BookingRepository;
import com.example.fms.booking.repository.PassengerRepository;
import com.example.fms.common.dto.EmailEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceImplTest {

    private FlightClient flightClient;
    private BookingRepository bookingRepo;
    private PassengerRepository passengerRepo;
    private EmailProducer emailProducer;
    private CircuitBreakerFactory cbFactory;
    private CircuitBreaker breaker;

    private BookingServiceImpl service;

    @BeforeEach
    void setup() {

        flightClient = Mockito.mock(FlightClient.class);
        bookingRepo = Mockito.mock(BookingRepository.class);
        passengerRepo = Mockito.mock(PassengerRepository.class);
        emailProducer = Mockito.mock(EmailProducer.class);

        cbFactory = Mockito.mock(CircuitBreakerFactory.class);
        breaker = Mockito.mock(CircuitBreaker.class);

        // When circuit breaker is created, return our mock
        Mockito.when(cbFactory.create(Mockito.any())).thenReturn(breaker);

        // FINAL FIX — CircuitBreaker uses Supplier, not Callable
        Mockito.when(breaker.run(Mockito.any(), Mockito.any()))
                .thenAnswer(inv -> {
                    Object supplier = inv.getArgument(0); // lambda passed to run()
                    return ((java.util.function.Supplier<?>) supplier).get();
                });

        service = new BookingServiceImpl(flightClient, bookingRepo, passengerRepo, emailProducer, cbFactory);
    }

    @Test
    void testBookFlightSuccess() throws Exception {

        BookingRequest req = new BookingRequest();
        req.setFlightId("F1");
        req.setEmail("test@mail.com");
        req.setPassengers(List.of(new PassengerDTO("Joel", "M", 22, "12A", "VEG")));

        Mockito.when(flightClient.reserveSeats(Mockito.any(), Mockito.any()))
                .thenReturn(true);

        BookingResponse res = service.bookFlight(req);

        assertEquals("F1", res.getFlightId());
        assertEquals("BOOKED", res.getStatus());

        Mockito.verify(bookingRepo, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(passengerRepo, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(emailProducer, Mockito.times(1)).sendEmail(Mockito.any(EmailEvent.class));
    }

    @Test
    void testBookFlight_SeatReservationFails() throws Exception {

        BookingRequest req = new BookingRequest();
        req.setFlightId("F1");
        req.setPassengers(List.of(new PassengerDTO()));

        Mockito.when(flightClient.reserveSeats(Mockito.any(), Mockito.any()))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.bookFlight(req));
    }

    @Test
    void testCancelBooking() {

        Booking booking = new Booking();
        booking.setPnr("P1");
        booking.setEmail("test@mail.com");

        Mockito.when(bookingRepo.findByPnr("P1"))
                .thenReturn(booking);

        CancelResponse res = service.cancelBooking("P1");

        assertEquals("CANCELLED", res.getStatus());
        Mockito.verify(bookingRepo, Mockito.times(1)).save(booking);
        Mockito.verify(emailProducer, Mockito.times(1)).sendEmail(Mockito.any());
    }

    @Test
    void testCancelBookingInvalidPnr() {
        Mockito.when(bookingRepo.findByPnr("BAD")).thenReturn(null);
        assertThrows(RuntimeException.class, () -> service.cancelBooking("BAD"));
    }

    @Test
    void testGetTicketSuccess() {

        Booking booking = new Booking();
        booking.setId("B1");
        booking.setPnr("P1");
        booking.setFlightId("F1");
        booking.setEmail("test@mail.com");
        booking.setSeatsBooked(1);
        booking.setBookingTime(LocalDateTime.now());

        Passenger px = new Passenger();
        px.setBookingId("B1");
        px.setName("Joel");
        px.setGender("M");
        px.setAge(22);

        // FIXED Airline spelling to match expected "IndiGo"
        FlightInfoDTO info = new FlightInfoDTO(
                "F1", "6E101", "IndiGo", "HYD", "DEL",
                "10", 1202, 5000, "IndiGo"
        );

        Mockito.when(bookingRepo.findByPnr("P1"))
                .thenReturn(booking);
        Mockito.when(passengerRepo.findByBookingId("B1"))
                .thenReturn(List.of(px));
        Mockito.when(flightClient.getFlightDetails("F1"))
                .thenReturn(info);

        TicketResponse response = service.getTicket("P1");

        assertEquals("P1", response.getBooking().getPnr());
        assertEquals("Joel", response.getPassengers().get(0).getName());
        assertEquals("IndiGo", response.getFlight().getAirlineName());
    }

    @Test
    void testGetTicketInvalidPnr() {
        Mockito.when(bookingRepo.findByPnr("BAD"))
                .thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.getTicket("BAD"));
    }

    @Test
    void testGetHistory() {
        Mockito.when(bookingRepo.findByEmail("test@mail.com"))
                .thenReturn(List.of(new Booking()));

        Object res = service.getHistory("test@mail.com");

        assertTrue(res instanceof List);
    }
}
