package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.department.CursorPageResponseDepartmentDto;
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
import com.team01.hrbank.util.CursorUtil;
import java.util.List;
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
        Long employeeCount = employeeRepository.countByDepartmentId(savedDepartment.getId());

        return departmentMapper.toDto(savedDepartment, employeeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto getDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new EntityNotFoundException(DEPARTMENT, departmentId));

        Long employeeCount = employeeRepository.countByDepartmentId(departmentId);

        return departmentMapper.toDto(department, employeeCount);
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
        Long employeeCount = employeeRepository.countByDepartmentId(departmentId);

        return departmentMapper.toDto(department, employeeCount);
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

    @Override
    public CursorPageResponseDepartmentDto getDepartments(String nameOrDescription, Long idAfter,
        String cursor, int size, String sortField, String sortDirection) {

        // 1. cursor 디코딩 -> idAfter로 변환
        Long decodedIdAfter = (cursor != null && !cursor.isEmpty()) ? CursorUtil.decodeCursor(cursor) : idAfter;

        // 2. Repository 호출 (size + 1로 hasNext 확인)
        List<DepartmentDto> departmentList = departmentRepository.findDepartmentsWithConditions(
            nameOrDescription, decodedIdAfter, sortField, sortDirection, size + 1
        );

        // 3. hasNext 판단
        boolean hasNext = departmentList.size() > size;

        // 4. 실제 반환할 리스트 자르기
        List<DepartmentDto> content = hasNext
            ? departmentList.subList(0, size)
            : departmentList;

        // 5. nextIdAfter 계산
        Long nextIdAfter = hasNext && !content.isEmpty()
            ? content.get(content.size() - 1).id()
            : null;

        // 6. nextCursor 인코딩
        String nextCursor = CursorUtil.encodeCursor(nextIdAfter);

        // 7. 전체 요소 수 계산
        long totalElements = departmentRepository.count();

        // 8. 응답 DTO 생성
        return new CursorPageResponseDepartmentDto(
            content,
            nextCursor,
            nextIdAfter,
            size,
            totalElements,
            hasNext
        );
    }
}
