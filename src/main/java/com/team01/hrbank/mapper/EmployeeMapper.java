package com.team01.hrbank.mapper;

import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
  public EmployeeDto toDto(Employee employee) {
    return new EmployeeDto(
        employee.getId(),
        employee.getName(),
        employee.getEmail(),
        employee.getEmployeeNumber(),
        employee.getDepartment().getName(),
        employee.getPosition(),
        employee.getHireDate(),
        employee.getStatus().getDescription(),
        employee.getProfile().getId()
    );
  }
}
