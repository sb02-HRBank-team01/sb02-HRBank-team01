package com.team01.hrbank.repository.impl;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.hrbank.entity.ChangeLog;
import com.team01.hrbank.entity.QChangeLog;
import com.team01.hrbank.enums.ChangeType;
import com.team01.hrbank.repository.custom.ChangeLogQueryRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogRepositoryImpl implements ChangeLogQueryRepository {

    private final JPAQueryFactory queryFactory;
    private QChangeLog qChangeLog = QChangeLog.changeLog;

    @Override
    public List<ChangeLog> findByConditions(
        String employeeNumber, ChangeType type, String memo,
        String ipAddress, Instant atFrom, Instant atTo, Long idAfter,
        String sortField, String sortDirection,
        int size
    ) {
        BooleanBuilder where = new BooleanBuilder();

        if (employeeNumber != null && !employeeNumber.isBlank()) {
            where.and(qChangeLog.employeeNumber.containsIgnoreCase(employeeNumber));
        }

        // PostgreSQl에서 enum != varchar 변환 후 비교 필요
        if (type != null) {
            where.and(
                stringTemplate("cast({0} as string)", qChangeLog.type)
                    .eq(type.name())
            );
        }
        if (memo != null && !memo.isBlank()) {
            where.and(qChangeLog.memo.containsIgnoreCase(memo));
        }
        if (ipAddress != null && !ipAddress.isBlank()) {
            where.and(qChangeLog.ipAddress.containsIgnoreCase(ipAddress));
        }
        if (atFrom != null) {
            where.and(qChangeLog.updatedAt.goe(atFrom));
        }
        if (atTo != null) {
            where.and(qChangeLog.updatedAt.loe(atTo));
        }

        // 커서 기반 페이지네이션을 위해 필요
        if (idAfter != null) {
            if ("desc".equalsIgnoreCase(sortDirection)) {
                where.and(qChangeLog.id.lt(idAfter)); // 내림차순 → 작은 ID를 기준으로
            } else {
                where.and(qChangeLog.id.gt(idAfter)); // 오름차순 → 큰 ID를 기준으로
            }
        }

        // 프론트에서 오는 요청에 따라 asc, desc 모두 정렬 가능
        OrderSpecifier<?> order = buildOrderSpecifier(sortField, sortDirection);

        return queryFactory
            .selectFrom(qChangeLog)
            .where(where)
            .orderBy(order)
            .limit(size+1)
            .fetch();
    }

    @Override
    public long countByConditions(
        String employeeNumber, ChangeType type, String memo,
        String ipAddress, Instant atFrom, Instant atTo
    ) {
        BooleanBuilder where = new BooleanBuilder();

        if (employeeNumber != null && !employeeNumber.isBlank()) {
            where.and(qChangeLog.employeeNumber.containsIgnoreCase(employeeNumber));
        }
        if (type != null) {
            where.and(
                stringTemplate("cast({0} as string)", qChangeLog.type)
                    .eq(type.name())
            );
        }
        if (memo != null && !memo.isBlank()) {
            where.and(qChangeLog.memo.containsIgnoreCase(memo));
        }
        if (ipAddress != null && !ipAddress.isBlank()) {
            where.and(qChangeLog.ipAddress.containsIgnoreCase(ipAddress));
        }
        if (atFrom != null) {
            where.and(qChangeLog.updatedAt.goe(atFrom));
        }
        if (atTo != null) {
            where.and(qChangeLog.updatedAt.loe(atTo));
        }

        return queryFactory
            .select(qChangeLog.count())
            .from(qChangeLog)
            .where(where)
            .fetchOne();
    }

    // 정렬 기준과 방향을 받아서 QueryDSL의 OrderSpecifier 객체를 만드는 역할
    private OrderSpecifier<?> buildOrderSpecifier(String sortField, String sortDirection) {

        boolean asc = sortDirection == null || sortDirection.equalsIgnoreCase("asc");

        if(sortField == null || sortField.isBlank()){
            sortField = "at";
        }
        return switch (sortField) {
            case "ipAddress" -> asc ? qChangeLog.ipAddress.asc() : qChangeLog.ipAddress.desc();
            case "updatedAt", "at", "" -> asc ? qChangeLog.updatedAt.asc() : qChangeLog.updatedAt.desc();
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + sortField);
        };
    }
}
