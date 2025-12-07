package com.example.fms.flight.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchFlightResponse {
    private List<FlightInfoDTO> flights;
}
