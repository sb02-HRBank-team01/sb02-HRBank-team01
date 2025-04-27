package com.team01.hrbank.dto.backup;

import org.springframework.data.domain.Sort;


public record CursorRequest(
    Long idAfter,
    String cursor,
    int size,
    String sortField,
    Sort.Direction direction
) {}