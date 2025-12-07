package com.example.fms.booking.dto;

import lombok.Data;

@Data
public class CancelResponse {
    private String pnr;
    private String status;
}
