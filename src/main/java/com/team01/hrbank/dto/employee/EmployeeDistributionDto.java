package com.team01.hrbank.dto.employee;

public record EmployeeDistributionDto(
    String groupKey,
    Long count,
    Double percentage
) {

}
