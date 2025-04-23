package com.team01.hrbank.service.impl;

import com.team01.hrbank.constraint.EmployeeStatus;
import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.entity.BinaryContent;
import com.team01.hrbank.entity.Department;
import com.team01.hrbank.entity.Employee;
import com.team01.hrbank.exception.DuplicateException;
import com.team01.hrbank.mapper.EmployeeMapper;
import com.team01.hrbank.repository.DepartmentRepository;
import com.team01.hrbank.repository.EmployeeRepository;
import com.team01.hrbank.service.EmployeeService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public EmployeeDto save(EmployeeCreateRequest employeeCreateRequest, MultipartFile profile) throws IOException {
        if(employeeRepository.existsByEmail((employeeCreateRequest.email()))) {
            throw new DuplicateException(employeeCreateRequest.email());
        }

        Department department = departmentRepository.findById(employeeCreateRequest.departmentId())
            .orElseThrow(
                // 수정 예정
                () -> new NoSuchElementException("존재하지 않는 부서입니다. : " + employeeCreateRequest.departmentId())
            );

        BinaryContent binaryContent = null;
        if (profile != null && !profile.isEmpty()) {
            binaryContent = new BinaryContent(
                profile.getOriginalFilename(),
                (long) profile.getBytes().length,
                profile.getContentType()
            );
        }

        Employee employee = new Employee(
            employeeCreateRequest.name(),
            employeeCreateRequest.email(),
            department,
            employeeCreateRequest.position(),
            employeeCreateRequest.hireDate(),
            EmployeeStatus.ACTIVE,
            binaryContent
        );

        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    public CursorPageResponseEmployeeDto<EmployeeDto> findAll(
        String nameOrEmail, String employeeNumber, String departmentName,
        String position, LocalDate hireDateFrom, LocalDate hireDateTo,
        String status, String cursor, Long idAfter,
        int size, String sortField, String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(direction, sortField).and(Sort.by(direction, "id")));

        Slice<Employee> employees = employeeRepository.findEmployeesByCursor(
            nameOrEmail, employeeNumber, departmentName, position,
            hireDateFrom, hireDateTo, status,
            pageable
        );

        boolean hasNext = employees.getContent().size() > size;

        List<EmployeeDto> content = employees.getContent().stream()
            .limit(size)
            .map(employeeMapper::toDto)
            .toList();

        Employee last = hasNext ? employees.getContent().get(size) : null;
        String nextCursor = last != null ? getSortValue(last, sortField) : null;
        Long nextIdAfter = last != null ? last.getId() : null;

        return new CursorPageResponseEmployeeDto<>(
            content, nextCursor, nextIdAfter, size,
            employees.getNumberOfElements(), hasNext
        );
    }

    private String getSortValue(Employee employee, String sortField) {
        return switch (sortField) {
            case "name" -> employee.getName();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "hireDate" -> employee.getHireDate().toString();
            default -> null;
        };
    }
}
