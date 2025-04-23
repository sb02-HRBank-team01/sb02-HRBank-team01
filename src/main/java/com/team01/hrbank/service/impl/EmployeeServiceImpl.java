package com.team01.hrbank.service.impl;

import com.team01.hrbank.constraint.EmployeeStatus;
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
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
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
}
