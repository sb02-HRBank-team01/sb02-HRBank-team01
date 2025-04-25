package com.team01.hrbank.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.entity.QEmployee;
import com.team01.hrbank.enums.EmployeeStatus;
import com.team01.hrbank.repository.custom.EmployeeQueryRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeQueryRepository {

    private final JPAQueryFactory queryFactory;
    QEmployee employee = QEmployee.employee;

    @Override
    public CursorPageResponseEmployeeDto<EmployeeDto> findEmployeeByCursor(
        String nameOrEmail,
        String employeeNumber,
        String departmentName,
        String position,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 필터 조건
        if (nameOrEmail != null && !nameOrEmail.isBlank()) {
            builder.and(employee.name.containsIgnoreCase(nameOrEmail)
                .or(employee.email.containsIgnoreCase(nameOrEmail)));
        }
        if (employeeNumber != null && !employeeNumber.isBlank()) {
            builder.and(employee.employeeNumber.eq(employeeNumber));
        }
        if (departmentName != null && !departmentName.isBlank()) {
            builder.and(employee.department.name.eq(departmentName));
        }
        if (position != null && !position.isBlank()) {
            builder.and(employee.position.eq(position));
        }
        if (hireDateFrom != null) {
            builder.and(employee.hireDate.goe(hireDateFrom));
        }
        if (hireDateTo != null) {
            builder.and(employee.hireDate.loe(hireDateTo));
        }
        if (status != null) {
            builder.and(employee.status.eq(status));
        }

        // 커서 조건 (정렬 필드 + cursor + idAfter 기준)
        if (cursor != null) {
            switch (sortField) {
                case "employeeNumber" -> {
                    if ("desc".equalsIgnoreCase(sortDirection)) {
                        builder.and(
                            employee.employeeNumber.lt(cursor)
                                .or(employee.employeeNumber.eq(cursor).and(employee.id.lt(idAfter)))
                        );
                    } else {
                        builder.and(
                            employee.employeeNumber.gt(cursor)
                                .or(employee.employeeNumber.eq(cursor).and(employee.id.gt(idAfter)))
                        );
                    }
                }
                case "hireDate" -> {
                    LocalDate hireCursor = LocalDate.parse(cursor);
                    if ("desc".equalsIgnoreCase(sortDirection)) {
                        builder.and(
                            employee.hireDate.lt(hireCursor)
                                .or(employee.hireDate.eq(hireCursor).and(employee.id.lt(idAfter)))
                        );
                    } else {
                        builder.and(
                            employee.hireDate.gt(hireCursor)
                                .or(employee.hireDate.eq(hireCursor).and(employee.id.gt(idAfter)))
                        );
                    }
                }
                default -> {
                    if ("desc".equalsIgnoreCase(sortDirection)) {
                        builder.and(
                            employee.name.lt(cursor)
                                .or(employee.name.eq(cursor).and(employee.id.lt(idAfter)))
                        );
                    } else {
                        builder.and(
                            employee.name.gt(cursor)
                                .or(employee.name.eq(cursor).and(employee.id.gt(idAfter)))
                        );
                    }
                }
            }
        }

        // 정렬
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortField, sortDirection);

        // 데이터 조회
        List<EmployeeDto> results = queryFactory
            .select(Projections.constructor(EmployeeDto.class,
                employee.id,
                employee.name,
                employee.email,
                employee.employeeNumber,
                employee.department.name,
                employee.position,
                employee.hireDate,
                employee.status.stringValue(),
                employee.profile.id
            ))
            .from(employee)
            .where(builder)
            .orderBy(orderSpecifier, employee.id.asc()) // 정렬 필드 다음에 id 정렬
            .limit(size + 1)
            .fetch();

        boolean hasNext = results.size() > size;
        List<EmployeeDto> content = hasNext ? results.subList(0, size) : results;

        String nextCursor = null;
        Long nextId = null;
        if (hasNext) {
            EmployeeDto last = content.get(content.size() - 1);
            nextCursor = switch (sortField) {
                case "employeeNumber" -> last.employeeNumber();
                case "hireDate" -> last.hireDate().toString();
                default -> last.name();
            };
            nextId = last.id();
        }

        return new CursorPageResponseEmployeeDto<>(
            content, nextCursor, nextId, size, results.size(), hasNext
        );
    }

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
                fromDate != null ? employee.createdAt.goe(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                toDate != null ? employee.createdAt.loe(toDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()) : null
            )
            .fetchOne();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection) {
        Order order = "asc".equalsIgnoreCase(sortDirection) ? Order.ASC : Order.DESC;

        return switch (sortField) {
            case "employeeNumber" -> new OrderSpecifier<>(order, employee.employeeNumber);
            case "hireDate" -> new OrderSpecifier<>(order, employee.hireDate);
            default -> new OrderSpecifier<>(order, employee.name);
        };
    }
}
