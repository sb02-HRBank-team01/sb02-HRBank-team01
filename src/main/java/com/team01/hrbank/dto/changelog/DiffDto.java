package com.team01.hrbank.dto.changelog;

public record DiffDto(
    String propertyName,
    String before,
    String after
) {
}
