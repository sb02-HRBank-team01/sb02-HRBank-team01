package com.team01.hrbank.repository;

import com.team01.hrbank.entity.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByDepartmentId(Long departmentId); // 단순히 존재 여부만 판단(조회X)

    long countByDepartmentId(Long departmentId);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT e FROM Employee e " +
        "LEFT JOIN FETCH e.department " +
        "LEFT JOIN FETCH e.profile " +
        "WHERE e.id = :id")
    Optional<Employee> findWithDetailsById(@Param("id") Long id);

    @Query(value = """
        SELECT DATE_TRUNC(:unit, e.hire_date) AS date, COUNT(*) AS count
        FROM employees e
        WHERE e.hire_date BETWEEN :from AND :to
        GROUP BY date
        ORDER BY date
    """, nativeQuery = true)
    List<Object[]> countActiveGroupedByUnit(
        @Param("from") LocalDate from,
        @Param("to") LocalDate to,
        @Param("unit") String unit
    );


}
