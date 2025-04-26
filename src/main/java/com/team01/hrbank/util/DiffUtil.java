package com.team01.hrbank.util;

import com.team01.hrbank.dto.changelog.DiffDto;
import com.team01.hrbank.dto.employee.EmployeeUpdateRequest;
import com.team01.hrbank.entity.Employee;
import java.util.ArrayList;
import java.util.List;

public class DiffUtil {
    public static List<DiffDto> compare(Employee before, EmployeeUpdateRequest after) {
        List<DiffDto> diffs = new ArrayList<>();

        if (!before.getName().equals(after.name())) {
            diffs.add(new DiffDto("이름", before.getName(), after.name()));
        }
        if (!before.getEmail().equals(after.email())) {
            diffs.add(new DiffDto("이메일", before.getEmail(), after.email()));
        }
        if (!before.getDepartment().getId().equals(after.departmentId())) {
            diffs.add(new DiffDto("부서", String.valueOf(before.getDepartment().getId()), String.valueOf(after.departmentId())));
        }
        if (!before.getPosition().equals(after.position())) {
            diffs.add(new DiffDto("직책", before.getPosition(), after.position()));
        }
        if (!before.getHireDate().equals(after.hireDate())) {
            diffs.add(new DiffDto("입사일", before.getHireDate().toString(), after.hireDate().toString()));
        }
        if (!before.getStatus().name().equals(after.status())) {
            diffs.add(new DiffDto("상태", before.getStatus().name(), after.status()));
        }

        return diffs;
    }
}
