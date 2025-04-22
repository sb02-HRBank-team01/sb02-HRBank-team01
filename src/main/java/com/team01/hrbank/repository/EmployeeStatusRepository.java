package com.team01.hrbank.repository;

import com.team01.hrbank.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeStatusRepository extends JpaRepository<Employee, Long> {

}