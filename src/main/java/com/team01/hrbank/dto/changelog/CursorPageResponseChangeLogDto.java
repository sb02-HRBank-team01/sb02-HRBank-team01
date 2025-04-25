package com.team01.hrbank.dto.changelog;

import java.util.List;

public record CursorPageResponseChangeLogDto<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
}
