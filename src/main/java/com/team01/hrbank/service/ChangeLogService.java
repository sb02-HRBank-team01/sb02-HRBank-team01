package com.team01.hrbank.service;

import com.team01.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.team01.hrbank.dto.changelog.DiffDto;
import com.team01.hrbank.enums.ChangeType;
import java.time.Instant;
import java.util.List;

public interface ChangeLogService {
    // 이력 등록
    void save(
        ChangeType type,
        String employeeNumber,
        List<DiffDto> details,
        String memo,
        String ipAddress
    );

    CursorPageResponseChangeLogDto searchChangeLogs(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo,
        Long idAfter,
        String sortField,
        String sortDirection,
        int size
    );

    // 상세 변경 내역 조회
    List<DiffDto> findChangeDetails(Long changeLogId);

    // 수정 이력 건수 조회
    long countChangeLogs(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo
    );
}
