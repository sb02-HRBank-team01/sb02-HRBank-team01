package com.team01.hrbank.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.hrbank.dto.backup.BackupDto;
import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import com.team01.hrbank.entity.QBackup;
import com.team01.hrbank.enums.BackupStatus;
import com.team01.hrbank.repository.custom.BackupCustomRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class BackupRepositoryImpl implements BackupCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QBackup backup = QBackup.backup;

    private final JPAQueryFactory queryFactory;

    @Override
    public BackupPageDto search(String worker, String status, Instant startedAtFrom, Instant startedAtTo, CursorRequest cursorRequest) {
        BooleanBuilder condition = new BooleanBuilder();

        if (worker != null && !worker.isBlank()) {
            condition.and(backup.worker.eq(worker));
        }

        if (status != null && !status.isBlank()) {
            condition.and(backup.status.eq(BackupStatus.valueOf(status)));
        }

        if (startedAtFrom != null) {
            condition.and(backup.startedAt.goe(startedAtFrom));
        }

        if (startedAtTo != null) {
            condition.and(backup.startedAt.loe(startedAtTo));
        }

        Long countResult = queryFactory
            .select(backup.count())
            .from(backup)
            .where(condition)
            .fetchOne();

        long totalElements = (countResult != null) ? countResult : 0L;

        // 커서 조건 추가
        if (cursorRequest.cursor() != null) {
            Instant cursorTime = Instant.parse(cursorRequest.cursor());
            if (cursorRequest.direction() == Sort.Direction.ASC) {
                condition.and(backup.startedAt.gt(cursorTime));
            } else {
                condition.and(backup.startedAt.lt(cursorTime));
            }
        } else if (cursorRequest.idAfter() != null) {
            condition.and(backup.id.gt(cursorRequest.idAfter()));
        }

        // 정렬 필드
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(cursorRequest.sortField(), cursorRequest.direction());

        List<BackupDto> content = queryFactory
            .selectFrom(backup)
            .where(condition)
            .orderBy(orderSpecifier, backup.id.asc()) // 동일한 sortField 값일 때 id로 추가 정렬
            .limit(cursorRequest.size() + 1) // 다음 페이지 여부를 확인하기 위해 1개 더 조회
            .fetch()
            .stream()
            .map(b -> new BackupDto(
                b.getId(),
                b.getWorker(),
                b.getStartedAt(),
                b.getEndedAt(),
                b.getStatus().name(),
                b.getFile() != null ? b.getFile().getId() : null
            ))
            .collect(Collectors.toList());

        boolean hasNext = content.size() > cursorRequest.size();
        if (hasNext) {
            content = content.subList(0, cursorRequest.size()); // size 초과분 제거
        }

        String nextCursor = null;
        Long nextIdAfter = null;
        if (!content.isEmpty()) {
            BackupDto last = content.get(content.size() - 1);
            nextCursor = last.startedAt().toString();
            nextIdAfter = last.id();
        }

        return new BackupPageDto(content, nextCursor, nextIdAfter, cursorRequest.size(), totalElements, hasNext);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, Sort.Direction direction) {
        if (sortField.equalsIgnoreCase("startedAt")) {
            return direction == Sort.Direction.ASC ? backup.startedAt.asc() : backup.startedAt.desc();
        } else if (sortField.equalsIgnoreCase("endedAt")) {
            return direction == Sort.Direction.ASC ? backup.endedAt.asc() : backup.endedAt.desc();
        } else {
            // 기본 startedAt
            return backup.startedAt.desc();
        }
    }
}