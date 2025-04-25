package com.team01.hrbank.dto.employee;

import com.team01.hrbank.enums.EmployeeStatus;
import java.time.LocalDate;

public record CursorPageRequestEmployeeDto (
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
) {

}
