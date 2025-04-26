package com.team01.hrbank.repository.custom;

import com.team01.hrbank.dto.employee.CursorPageRequestEmployeeDto;
import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeTrendDto;
import com.team01.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeQueryRepository {
  CursorPageResponseEmployeeDto<EmployeeDto> findEmployeeByCursor(CursorPageRequestEmployeeDto request);
  List<EmployeeDistributionDto> findDistributionBy(String groupBy, EmployeeStatus status);
  List<EmployeeTrendDto> findEmployeeTrend(LocalDate from, LocalDate to, String unit);
  Long employeeCountBy(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}

