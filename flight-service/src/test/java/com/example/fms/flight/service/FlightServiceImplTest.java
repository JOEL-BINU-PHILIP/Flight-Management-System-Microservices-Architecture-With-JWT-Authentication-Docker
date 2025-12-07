package com.example.fms.flight.service;

import com.example.fms.flight.dto.*;
import com.example.fms.flight.model.Airline;
import com.example.fms.flight.model.Flight;
import com.example.fms.flight.repository.AirlineRepository;
import com.example.fms.flight.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FlightServiceImplTest {

    private AirlineRepository airlineRepo;
    private FlightRepository flightRepo;
    private FlightServiceImpl service;

    @BeforeEach
    void setup() {
        airlineRepo = Mockito.mock(AirlineRepository.class);
        flightRepo = Mockito.mock(FlightRepository.class);
        service = new FlightServiceImpl(airlineRepo, flightRepo);
    }

    @Test
    void testAddAirlineSuccess() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setName("IndiGo");
        req.setLogoUrl("logo.png");

        Mockito.when(airlineRepo.existsByNameIgnoreCase("IndiGo"))
                .thenReturn(false);

        assertDoesNotThrow(() -> service.addAirline(req));
        Mockito.verify(airlineRepo, Mockito.times(1))
                .save(Mockito.any(Airline.class));
    }

    @Test
    void testAddAirlineDuplicate() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setName("IndiGo");

        Mockito.when(airlineRepo.existsByNameIgnoreCase("IndiGo"))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.addAirline(req));
    }

    @Test
    void testAddInventorySuccess() {
        AddInventoryRequest req = new AddInventoryRequest();
        req.setAirlineId("A1");
        req.setFlightNumber("6E101");
        req.setFromPlace("HYD");
        req.setToPlace("DEL");
        req.setDepartureTime("2025-01-01T10:00:00");
        req.setArrivalTime("2025-01-01T12:00:00");
        req.setPrice(5000);
        req.setTotalSeats(100);

        Mockito.when(airlineRepo.findById("A1"))
                .thenReturn(Optional.of(new Airline()));
        Mockito.when(flightRepo.existsByFlightNumberIgnoreCase("6E101"))
                .thenReturn(false);

        assertDoesNotThrow(() -> service.addInventory(req));
        Mockito.verify(flightRepo, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testAddInventoryAirlineNotFound() {
        AddInventoryRequest req = new AddInventoryRequest();
        req.setAirlineId("A1");

        Mockito.when(airlineRepo.findById("A1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.addInventory(req));
    }

    @Test
    void testAddInventoryDuplicateFlight() {
        AddInventoryRequest req = new AddInventoryRequest();
        req.setAirlineId("A1");
        req.setFlightNumber("6E101");

        Mockito.when(airlineRepo.findById("A1"))
                .thenReturn(Optional.of(new Airline()));
        Mockito.when(flightRepo.existsByFlightNumberIgnoreCase("6E101"))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.addInventory(req));
    }

    @Test
    void testGetFlightDetails() {

        Flight f = new Flight();
        f.setId("F1");
        f.setFlightNumber("6E101");
        f.setFromPlace("HYD");
        f.setToPlace("DEL");
        f.setDepartureTime(LocalDateTime.now());
        f.setArrivalTime(LocalDateTime.now().plusHours(2));
        f.setAvailableSeats(50);
        f.setPrice(5000);
        f.setAirlineId("A1");

        Airline airline = new Airline();
        airline.setId("A1");
        airline.setName("IndiGo");

        Mockito.when(flightRepo.findById("F1"))
                .thenReturn(Optional.of(f));
        Mockito.when(airlineRepo.findById("A1"))
                .thenReturn(Optional.of(airline));

        FlightDTO dto = service.getFlightDetails("F1");

        assertEquals("6E101", dto.getFlightNumber());
        assertEquals("IndiGo", dto.getAirlineName());
    }

    @Test
    void testSearchFlights() {

        SearchFlightRequest req = new SearchFlightRequest();
        req.setFromPlace("HYD");
        req.setToPlace("DEL");
        req.setDate("2025-01-01");

        Flight f = new Flight();
        f.setId("F1");
        f.setFlightNumber("6E101");
        f.setFromPlace("HYD");
        f.setToPlace("DEL");
        f.setAvailableSeats(50);
        f.setPrice(5000);
        f.setAirlineId("A1");
        f.setDepartureTime(LocalDateTime.now());
        f.setArrivalTime(LocalDateTime.now());

        Airline airline = new Airline();
        airline.setId("A1");
        airline.setName("IndiGo");

        Mockito.when(flightRepo.findByFromPlaceAndToPlaceAndDepartureTimeBetween(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(List.of(f));

        Mockito.when(airlineRepo.findById("A1"))
                .thenReturn(Optional.of(airline));

        SearchFlightResponse res = service.searchFlights(req);

        assertEquals(1, res.getFlights().size());
        assertEquals("IndiGo", res.getFlights().get(0).getAirlineName());
    }

    @Test
    void testReserveSeatsSuccess() {
        Flight f = new Flight();
        f.setId("F1");
        f.setAvailableSeats(10);

        Mockito.when(flightRepo.findById("F1"))
                .thenReturn(Optional.of(f));

        boolean result = service.reserveSeats("F1", 5);

        assertTrue(result);
        assertEquals(5, f.getAvailableSeats());
    }

    @Test
    void testReserveSeatsInsufficient() {
        Flight f = new Flight();
        f.setAvailableSeats(2);

        Mockito.when(flightRepo.findById("F1"))
                .thenReturn(Optional.of(f));

        boolean result = service.reserveSeats("F1", 5);

        assertFalse(result);
    }
}
