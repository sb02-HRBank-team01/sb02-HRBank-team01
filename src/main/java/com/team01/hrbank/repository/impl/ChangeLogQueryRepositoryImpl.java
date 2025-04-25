package com.team01.hrbank.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.hrbank.entity.ChangeLog;
import com.team01.hrbank.entity.QChangeLog;
import com.team01.hrbank.enums.ChangeType;
import com.team01.hrbank.mapper.ChangeLogMapper;
import com.team01.hrbank.repository.custom.ChangeLogQueryRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor // JPAQueryFactory 주입
public class ChangeLogQueryRepositoryImpl implements ChangeLogQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final ChangeLogMapper changeLogMapper;
    private QChangeLog q = QChangeLog.changeLog;

    @Override
    public List<ChangeLog> findByConditions(
        String employeeNumber, ChangeType type, String memo,
        String ipAddress, Instant atFrom, Instant atTo, Long idAfter,
        String sortField, String sortDirection,
        int size
    ) {
        // 조건은 동적으로 누적해나갈 수 있다.
        // 사용자가 입력했을 수도 안 했을 수도 있기 때문에
        BooleanBuilder where = new BooleanBuilder();

        if (employeeNumber != null && !employeeNumber.isBlank()) {
            where.and(q.employeeNumber.containsIgnoreCase(employeeNumber));
        }
        if (type != null) {
            where.and(q.type.eq(type));
        }
        // 부분 검색이 가능
        // employeeNumber LIKE %xxx% 와 비슷
        if (memo != null && !memo.isBlank()) {
            where.and(q.memo.containsIgnoreCase(memo));
        }
        if (ipAddress != null && !ipAddress.isBlank()) {
            where.and(q.ipAddress.containsIgnoreCase(ipAddress));
        }
        if (atFrom != null) {
            where.and(q.updatedAt.goe(atFrom));
        }
        if (atTo != null) {
            where.and(q.updatedAt.loe(atTo));
        }
        // 커서 기반 페이지네이션을 위해 필요하다.
        // 이전 페이지 마지막 ID보다 큰 항목 만 조호히하여 다음 페이지를 구현할 수 있도록 한다.
        if (idAfter != null) {
            where.and(q.id.gt(idAfter));
        }

        // 정렬 조건
        // 프론트에서 오는 요청에 따라 asc, desc 모두 정렬 가능하다.
        OrderSpecifier<?> order = buildOrderSpecifier(sortField, sortDirection);

        return queryFactory
            .selectFrom(q)
            .where(where)
            .orderBy(order)
            .limit(size+1) // 다음 페이지 여부 판단용
            .fetch();
    }

    @Override
    public long countByConditions(
        String employeeNumber, ChangeType type, String memo,
        String ipAddress, Instant atFrom, Instant atTo
    ) {
        BooleanBuilder where = new BooleanBuilder();

        if (employeeNumber != null && !employeeNumber.isBlank()) {
            where.and(q.employeeNumber.containsIgnoreCase(employeeNumber));
        }
        if (type != null) {
            where.and(q.type.eq(type));
        }
        if (memo != null && !memo.isBlank()) {
            where.and(q.memo.containsIgnoreCase(memo));
        }
        if (ipAddress != null && !ipAddress.isBlank()) {
            where.and(q.ipAddress.containsIgnoreCase(ipAddress));
        }
        if (atFrom != null) {
            where.and(q.updatedAt.goe(atFrom));
        }
        if (atTo != null) {
            where.and(q.updatedAt.loe(atTo));
        }

        return queryFactory
            .select(q.count())
            .from(q)
            .where(where)
            .fetchOne();
    }

    private OrderSpecifier<?> buildOrderSpecifier(String sortField, String sortDirection) {
        boolean asc = sortDirection == null || sortDirection.equalsIgnoreCase("asc");

        if ("ipAddress".equals(sortField)) {
            return asc ? q.ipAddress.asc() : q.ipAddress.desc();
        }
        // default: updatedAt
        return asc ? q.updatedAt.asc() : q.updatedAt.desc();
    }
}
