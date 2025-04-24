package com.team01.hrbank.repository;

import com.team01.hrbank.entity.ChangeLog;
import com.team01.hrbank.enums.ChangeType;
import java.time.Instant;
import java.util.List;

public interface ChangeLogRepositoryCustom {
    List<ChangeLog> findAllByConditions(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo,
        Long idAfter,
        int size,
        String sortField,
        String sortDirection
    );

    long countByConditions(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo
    );
}
