package com.team01.hrbank.service;

import com.team01.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.team01.hrbank.dto.department.DepartmentCreateRequest;
import com.team01.hrbank.dto.department.DepartmentDto;
import com.team01.hrbank.dto.department.DepartmentUpdateRequest;

public interface DepartmentService {

    DepartmentDto createDepartment(DepartmentCreateRequest request);

    DepartmentDto getDepartment(Long departmentId);

    DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest request);

    void deleteDepartment(Long departmentId);

    CursorPageResponseDepartmentDto getDepartments(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection
    );
}
