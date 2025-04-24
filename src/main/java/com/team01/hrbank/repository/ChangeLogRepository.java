package com.team01.hrbank.repository;

import com.team01.hrbank.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>, ChangeLogRepositoryCustom {
    //
    boolean existsByEmployeeNumber(String employeeNumber);
}
