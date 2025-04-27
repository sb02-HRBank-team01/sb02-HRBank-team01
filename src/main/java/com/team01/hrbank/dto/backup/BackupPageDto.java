package com.team01.hrbank.dto.backup;
import java.util.List;
public record BackupPageDto(
    List<BackupDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {} 