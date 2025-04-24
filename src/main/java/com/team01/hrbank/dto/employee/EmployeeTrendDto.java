package com.team01.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeTrendDto (
    LocalDate date,
    Long count,
    Long change,
    Double changeRate
) {

}
