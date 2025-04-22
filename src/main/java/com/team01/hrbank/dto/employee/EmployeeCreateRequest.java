package com.team01.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeCreateRequest (
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    String memo
) {

}
