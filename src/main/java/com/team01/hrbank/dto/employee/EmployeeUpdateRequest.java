package com.team01.hrbank.dto.employee;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record EmployeeUpdateRequest(
    @NotBlank(message = "직원명은 필수 입니다.")
    String name,

    @NotBlank(message = "이메일은 필수 입니다.")
    String email,

    @NotBlank(message = "부서는 필수 입니다.")
    Long departmentId,

    @NotBlank(message = "직급은 필수 입니다.")
    String position,

    @NotBlank(message = "입사일은 필수 입니다.")
    LocalDate hireDate,

    @NotBlank(message = "재직 상태는 필수 입니다.")
    String status,

    String memo
) {

}
