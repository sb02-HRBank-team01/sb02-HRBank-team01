package com.team01.hrbank.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DepartmentCreateRequest(
    @NotBlank(message = "부서명은 필수입니다.")
    @Size(max = 30, message = "부서명은 30자를 초과할 수 없습니다.")
    String name,

    @Size(max = 1000, message = "부서 설명은 1000자를 초과할 수 없습니다.")
    String description,

    @PastOrPresent(message = "설립일은 오늘 이전이어야 합니다.")
    LocalDate establishedDate
) {}
