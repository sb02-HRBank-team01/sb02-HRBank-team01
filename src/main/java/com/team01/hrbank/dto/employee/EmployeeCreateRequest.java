package com.team01.hrbank.dto.employee;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record EmployeeCreateRequest (
    @NotBlank(message = "직원 명은 필수 입니다.")
    String name,

    @NotBlank(message = "이메일은 필수 값입니다.")
    String email,

    Long departmentId,

    @NotBlank(message = "직급은 필수 입니다.")
    String position,

    LocalDate hireDate,

    String memo
) {

}
