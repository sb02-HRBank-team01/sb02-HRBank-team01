package com.team01.hrbank.repository.custom;

import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.enums.EmployeeStatus;
import java.util.List;

public interface EmployeeQueryRepository {
  List<EmployeeDistributionDto> findDistributionBy(String groupBy, EmployeeStatus status);
}
