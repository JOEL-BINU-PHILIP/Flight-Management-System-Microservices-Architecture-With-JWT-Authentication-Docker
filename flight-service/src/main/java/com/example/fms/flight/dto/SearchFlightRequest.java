package com.example.fms.flight.dto;

import lombok.Data;

@Data
public class SearchFlightRequest {
    private String fromPlace;
    private String toPlace;
    private String date;
}

