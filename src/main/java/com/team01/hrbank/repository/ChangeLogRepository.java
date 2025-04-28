package com.team01.hrbank.repository;

import com.team01.hrbank.entity.ChangeLog;
import com.team01.hrbank.repository.custom.ChangeLogQueryRepository;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>, ChangeLogQueryRepository {
    boolean existsByCreatedAtAfter(Instant createdAtAfter);
    @Query("SELECT MAX(c.createdAt) FROM ChangeLog c")
    Instant findLatestCreatedAt();
}

