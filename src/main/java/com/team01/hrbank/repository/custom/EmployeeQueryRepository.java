package com.team01.hrbank.repository.custom;

import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeQueryRepository {
  List<EmployeeDistributionDto> findDistributionBy(String groupBy, EmployeeStatus status);
  Long employeeCountBy(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
