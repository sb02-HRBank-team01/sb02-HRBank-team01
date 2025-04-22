package com.team01.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeDto (
  Long id,
  String name,
  String email,
  String employeeNumber,
  String departmentName,
  String position,
  LocalDate hireDate,
  String status,
  Long profileImageId
) {

}
