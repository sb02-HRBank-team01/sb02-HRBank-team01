package com.team01.hrbank.dto.changelog;

import com.team01.hrbank.enums.ChangeType;
import java.time.Instant;

public record ChangeLogDto(
    Long id,
    ChangeType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {
}
