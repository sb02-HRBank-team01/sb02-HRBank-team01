package com.team01.hrbank.service;

import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDto;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  EmployeeDto save(EmployeeCreateRequest employeeCreateRequest, MultipartFile profile) throws IOException;
}
