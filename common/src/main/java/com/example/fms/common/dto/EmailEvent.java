package com.example.fms.common.dto;

import java.io.Serializable;

public record EmailEvent(
        String to,
        String subject,
        String body,
        String eventType,
        String pnr
) implements Serializable {}

