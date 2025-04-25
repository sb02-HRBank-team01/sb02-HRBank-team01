package com.team01.hrbank.repository.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.entity.QEmployee;
import com.team01.hrbank.enums.EmployeeStatus;
import com.team01.hrbank.repository.custom.EmployeeQueryRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeQueryRepositoryImpl implements EmployeeQueryRepository {

    private final JPAQueryFactory queryFactory;
    QEmployee employee = QEmployee.employee;

    @Override
    public List<EmployeeDistributionDto> findDistributionBy(String groupBy, EmployeeStatus status) {
        Expression<String> groupExpression = groupBy.equals("position")
            ? employee.position
            : employee.department.name;

        // 전체 수 (percent 계산용)
        long totalCount = queryFactory
            .select(employee.count())
            .from(employee)
            .where(employee.status.eq(status))
            .fetchOne();

        if (totalCount == 0) return List.of();

        return queryFactory
            .select(Projections.constructor(
                EmployeeDistributionDto.class,
                groupExpression,
                employee.count(),
                employee.count().doubleValue().divide((double) totalCount).multiply(100)
            ))
            .from(employee)
            .where(employee.status.eq(status))
            .groupBy(groupExpression)
            .orderBy(employee.count().desc())
            .fetch();
    }

    @Override
    public Long employeeCountBy(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return queryFactory
            .select(employee.count())
            .from(employee)
            .where(
                employee.status.eq(status),
//                employee.createdAt.goe(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
//                employee.createdAt.loe(toDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
                fromDate != null ? employee.createdAt.goe(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                toDate != null ? employee.createdAt.loe(toDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()) : null
            )
            .fetchOne();
    }
}
