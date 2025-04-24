package com.team01.hrbank.repository.impl;

import com.team01.hrbank.repository.ChangeLogRepositoryCustom;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;

public class ChangeLogRepositoryImpl implements ChangeLogRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChangeLog> findAllByConditions(
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
    ) {
        // 추후 QueryDSL 조건 작성 예정
        return List.of(); // 빈 리스트 리턴 (임시)
    }

    @Override
    public long countByConditions(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo
    ) {
        // 추후 QueryDSL 조건 작성 예정
        return 0L;
    }
}
