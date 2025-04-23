package com.team01.hrbank.service;

import com.team01.hrbank.dto.department.DepartmentCreateRequest;
import com.team01.hrbank.dto.department.DepartmentDto;
import com.team01.hrbank.entity.Department;
import com.team01.hrbank.exception.DuplicateDepartmentNameException;
import com.team01.hrbank.mapper.DepartmentMapper;
import com.team01.hrbank.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateRequest request) {

        if (departmentRepository.existsByName(request.name())) {
            throw new DuplicateDepartmentNameException("이미 존재하는 부서명입니다.");
        }

        Department department = new Department(
            request.name(),
            request.description(),
            request.establishedDate()
        );
        Department savedDepartment = departmentRepository.save(department);

        // 나중에 0L 부분을 실제 직원을 조회한 값으로 바꿔야 합니다. + 조회 로직까지 추가
        return departmentMapper.toDto(savedDepartment, 0L);

    }

}
