package com.team01.hrbank.service;

import com.team01.hrbank.constraint.EmployeeStatus;
import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.entity.Employee;
import com.team01.hrbank.mapper.EmployeeMapper;
import com.team01.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @Override
  @Transactional
  public EmployeeDto save(EmployeeCreateRequest employeeCreateRequest) {
    if(!employeeRepository.existsByEmail((employeeCreateRequest.email()))) {
      throw new IllegalArgumentException("Email already exists");
    }

    Employee employee = new Employee(
        employeeCreateRequest.name(),
        employeeCreateRequest.email(),
        null,
        employeeCreateRequest.position(),
        employeeCreateRequest.hireDate(),
        EmployeeStatus.ACTIVE,
        null
    );

    return employeeMapper.toDto(employeeRepository.save(employee));
  }
}
