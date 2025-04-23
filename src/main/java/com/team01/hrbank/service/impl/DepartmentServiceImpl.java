package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.department.DepartmentCreateRequest;
import com.team01.hrbank.dto.department.DepartmentDto;
import com.team01.hrbank.dto.department.DepartmentUpdateRequest;
import com.team01.hrbank.entity.Department;
import com.team01.hrbank.exception.DuplicateException;
import com.team01.hrbank.exception.EntityNotFoundException;
import com.team01.hrbank.mapper.DepartmentMapper;
import com.team01.hrbank.repository.DepartmentRepository;
import com.team01.hrbank.repository.EmployeeRepository;
import com.team01.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeRepository employeeRepository;
    private static final String DEPARTMENT = "부서";

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateRequest request) {

        if (departmentRepository.existsByName(request.name())) {
            throw new DuplicateException(request.name());
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

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new EntityNotFoundException(DEPARTMENT, departmentId));

        if (departmentRepository.existsByNameAndIdNot(request.name(), departmentId)) {
            throw new DuplicateException(request.name());
        }

        department.update(request.name(), request.description(), request.establishedDate());

        return departmentMapper.toDto(department, 0L);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new EntityNotFoundException(DEPARTMENT, departmentId));

        if (employeeRepository.existsByDepartmentId(departmentId)) {
            throw new IllegalStateException("해당 부서에 소속된 직원이 있어 삭제할 수 없습니다.");
        }

        departmentRepository.delete(department);

    }
}
