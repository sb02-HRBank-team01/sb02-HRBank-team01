package com.team01.hrbank.service;

import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeUpdateRequest;
import java.io.IOException;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  EmployeeDto save(EmployeeCreateRequest employeeCreateRequest, MultipartFile profile) throws IOException;
  CursorPageResponseEmployeeDto<EmployeeDto> findAll(
      String nameOrEmail, String employeeNumber, String departmentName,
      String position, LocalDate hireDateFrom, LocalDate hireDateTo,
      String status, String cursor, Long idAfter,
      int size, String sortField, String sortDirection
  );
  EmployeeDto findById(Long id);
  EmployeeDto update(EmployeeUpdateRequest updateRequest, Long id, MultipartFile profile) throws IOException;
  void delete(Long id);
}
