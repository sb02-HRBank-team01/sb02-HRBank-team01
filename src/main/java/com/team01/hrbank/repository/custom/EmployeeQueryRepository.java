package com.team01.hrbank.repository.custom;

import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeQueryRepository {
  CursorPageResponseEmployeeDto<EmployeeDto> findEmployeeByCursor(
      String nameOrEmail,
      String employeeNumber,
      String departmentName,
      String position,
      LocalDate hireDateFrom,
      LocalDate hireDateTo,
      EmployeeStatus status,
      Long idAfter,
      String cursor,
      Integer size,
      String sortField,
      String sortDirection
  );
  List<EmployeeDistributionDto> findDistributionBy(String groupBy, EmployeeStatus status);
  Long employeeCountBy(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
