package com.team01.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    String status,
    String memo
) {

}
