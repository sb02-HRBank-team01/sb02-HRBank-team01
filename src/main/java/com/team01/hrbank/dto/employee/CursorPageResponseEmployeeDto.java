package com.team01.hrbank.dto.employee;

import java.util.List;

public record CursorPageResponseEmployeeDto<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
