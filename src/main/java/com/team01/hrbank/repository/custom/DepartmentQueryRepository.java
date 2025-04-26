package com.team01.hrbank.repository.custom;

import com.team01.hrbank.dto.department.DepartmentDto;
import java.util.List;

public interface DepartmentQueryRepository {

    List<DepartmentDto> findDepartmentsWithConditions(
        String nameOrDescription, String cursor, Long idAfter,
        String sortField, String sortDirection, int size
    );
}
