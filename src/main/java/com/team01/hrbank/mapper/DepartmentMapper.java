package com.team01.hrbank.mapper;

import com.team01.hrbank.dto.department.DepartmentDto;
import com.team01.hrbank.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentDto toDto(Department department, Long employeeCount) {
        return new DepartmentDto(
            department.getId(),
            department.getName(),
            department.getDescription(),
            department.getEstablishedDate(),
            employeeCount
        );
    }
}
