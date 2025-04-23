package com.team01.hrbank.repository;

import com.team01.hrbank.constraint.EmployeeStatus;
import com.team01.hrbank.entity.Employee;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByEmail(String email);

  @Query("""
    SELECT e FROM Employee e
    WHERE (:nameOrEmail IS NULL OR e.name LIKE %:nameOrEmail% OR e.email LIKE %:nameOrEmail%)
      AND (:employeeNumber IS NULL OR e.employeeNumber = :employeeNumber)
      AND (:departmentName IS NULL OR e.department.name = :departmentName)
      AND (:position IS NULL OR e.position = :position)
      AND (:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom)
      AND (:hireDateTo IS NULL OR e.hireDate <= :hireDateTo)
      AND (:status IS NULL OR e.status = :status)
  """)
  Slice<Employee> findEmployeesByCursor(
      @Param("nameOrEmail") String nameOrEmail,
      @Param("employeeNumber") String employeeNumber,
      @Param("departmentName") String departmentName,
      @Param("position") String position,
      @Param("hireDateFrom") LocalDate hireDateFrom,
      @Param("hireDateTo") LocalDate hireDateTo,
      @Param("status") String status,
      Pageable pageable
  );
}