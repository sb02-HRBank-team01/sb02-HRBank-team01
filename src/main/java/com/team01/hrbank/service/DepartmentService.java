package com.team01.hrbank.service;

import com.team01.hrbank.dto.department.DepartmentCreateRequest;
import com.team01.hrbank.dto.department.DepartmentDto;

public interface DepartmentService {

    DepartmentDto createDepartment(DepartmentCreateRequest request);
}
