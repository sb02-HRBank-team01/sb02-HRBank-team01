package com.team01.hrbank.dto.error;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String message,
    String details
) {}
