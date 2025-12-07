package com.example.fms.flight.dto;

import lombok.Data;

@Data
public class ReserveSeatRequest {
    private int seats;   // number of seats to reserve
}
