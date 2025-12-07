package com.example.fms.booking.util;

import java.util.UUID;

public class PnrGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
