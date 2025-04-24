package com.team01.hrbank.repository;

import com.team01.hrbank.entity.ChangeLog;
import com.team01.hrbank.enums.ChangeType;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    @Query("""
        SELECT c FROM ChangeLog c
        WHERE (:employeeNumber IS NULL OR c.employeeNumber LIKE %:employeeNumber%)
          AND (:type IS NULL OR c.type = :type)
          AND (:memo IS NULL OR c.memo LIKE %:memo%)
          AND (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%)
          AND (:atFrom IS NULL OR c.updatedAt >= :atFrom)
          AND (:atTo IS NULL OR c.updatedAt <= :atTo)
          AND (:idAfter IS NULL OR c.id > :idAfter)
        ORDER BY 
            CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'asc' THEN c.ipAddress END ASC,
            CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'desc' THEN c.ipAddress END DESC,
            CASE WHEN :sortField = 'at' AND :sortDirection = 'asc' THEN c.updatedAt END ASC,
            CASE WHEN :sortField = 'at' AND :sortDirection = 'desc' THEN c.updatedAt END DESC
        """)
    /*
    검색창에 사번이 입력되었으면 해당 키워드를 포함한 데이터만 조회
    WHERE (:employeeNumber IS NULL OR c.employeeNumber LIKE %:employeeNumber%)
          (Created, Updated, Deleted)type이 지정되었을 때만 필터링
          AND (:type IS NULL OR c.type = :type)
          메모가 입력된 경우 부분일치로 검색
          AND (:memo IS NULL OR c.memo LIKE %:memo%)
          IP주소 부분 검색
          AND (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%)
          시작 시간 이후의 로그만 조회
          AND (:atFrom IS NULL OR c.updatedAt >= :atFrom)
          종료 시간 이전의 로그만 조회
          AND (:atTo IS NULL OR c.updatedAt <= :atTo)
          커서기반 페이징 구현 (이전 페이지 마지막 ID기준)
          AND (:idAfter IS NULL OR c.id > :idAfter)
        ORDER BY
            정렬 필드가 ipAddress이고 정렬 방향이 asc일 경우
            CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'asc' THEN c.ipAddress END ASC,
            CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'desc' THEN c.ipAddress END DESC,
            CASE WHEN :sortField = 'at' AND :sortDirection = 'asc' THEN c.updatedAt END ASC,
            CASE WHEN :sortField = 'at' AND :sortDirection = 'desc' THEN c.updatedAt END DESC
     */

    /*
    List -> Slice
    다음 페이지 유무, 현재 페이지 정보 포함 (hasNext, size 등)
    자동 정렬 제공
    수동으로 idAfter로 필터링할 필요없이 Pageable을 통해 size, page 번호를 처리할 수 있다.
    무한 스크롤을 위해서도 필요함
    */
//    Slice<ChangeLog> findByConditionsByCursor(
//        @Param("employeeNumber") String employeeNumber,
//        @Param("type") ChangeType type,
//        @Param("memo") String memo,
//        @Param("ipAddress") String ipAddress,
//        @Param("atFrom") Instant atFrom,
//        @Param("atTo") Instant atTo,
//        @Param("idAfter") Long idAfter,
//        Pageable pageable
//    );
    List<ChangeLog> findByConditions(
        String employeeNumber, ChangeType type, String memo,
        String ipAddress, Instant atFrom, Instant atTo
    );
}

