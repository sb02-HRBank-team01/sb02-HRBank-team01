package com.team01.hrbank.exception;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String message,
    String details
) {}
