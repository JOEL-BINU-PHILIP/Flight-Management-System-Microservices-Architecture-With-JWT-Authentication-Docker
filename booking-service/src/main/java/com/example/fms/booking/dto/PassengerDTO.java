package com.example.fms.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerDTO {
    private String name;
    private String gender;
    private int age;
    private String seatNumber;
    private String meal;
}
