package com.team01.hrbank.service;

import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDto;

public interface EmployeeService {
  EmployeeDto save(EmployeeCreateRequest employeeCreateRequest);
}
