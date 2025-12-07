package com.example.fms.booking.controller;

import com.example.fms.booking.dto.*;
import com.example.fms.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.fail-fast=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void testBookFlight() throws Exception {

        BookingResponse response = new BookingResponse();
        response.setPnr("PNR123");
        response.setStatus("BOOKED");

        when(bookingService.bookFlight(any())).thenReturn(response);

        String json = """
                {
                    "flightId":"F1",
                    "email":"test@mail.com",
                    "passengers":[
                        {"name":"Joel","gender":"M","age":22,"seatNumber":"12A","meal":"VEG"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/book/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnr").value("PNR123"))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        verify(bookingService, times(1)).bookFlight(any());
    }

    @Test
    void testCancelBooking() throws Exception {

        CancelResponse cancel = new CancelResponse();
        cancel.setPnr("PNR123");
        cancel.setStatus("CANCELLED");

        when(bookingService.cancelBooking("PNR123"))
                .thenReturn(cancel);

        mockMvc.perform(delete("/api/book/cancel/PNR123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(bookingService, times(1)).cancelBooking("PNR123");
    }

    @Test
    void testGetTicket() throws Exception {

        TicketResponse ticket = TicketResponse.builder()
                .booking(BookingDTO.builder()
                        .pnr("PNR123")
                        .flightId("F1")
                        .email("test@mail.com")
                        .build())
                .passengers(List.of(
                        new PassengerDTO("Joel", "M", 22, "12A", "VEG")
                ))
                .flight(new com.example.fms.booking.dto.FlightInfoDTO())
                .build();

        when(bookingService.getTicket("PNR123"))
                .thenReturn(ticket);

        mockMvc.perform(get("/api/book/ticket/PNR123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booking.pnr").value("PNR123"));

        verify(bookingService, times(1)).getTicket("PNR123");
    }

    @Test
    void testGetHistory() throws Exception {

        when(bookingService.getHistory("test@mail.com"))
                .thenReturn(List.of("dummy"));

        mockMvc.perform(get("/api/book/history/test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("dummy"));

        verify(bookingService, times(1)).getHistory("test@mail.com");
    }
}
