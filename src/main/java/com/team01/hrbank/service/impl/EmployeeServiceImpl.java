package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.employee.CursorPageRequestEmployeeDto;
import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeTrendDto;
import com.team01.hrbank.dto.employee.EmployeeUpdateRequest;
import com.team01.hrbank.entity.BinaryContent;
import com.team01.hrbank.entity.Department;
import com.team01.hrbank.entity.Employee;
import com.team01.hrbank.enums.EmployeeStatus;
import com.team01.hrbank.enums.TimeUnit;
import com.team01.hrbank.exception.DuplicateException;
import com.team01.hrbank.exception.EntityNotFoundException;
import com.team01.hrbank.mapper.EmployeeMapper;
import com.team01.hrbank.repository.BinaryContentRepository;
import com.team01.hrbank.repository.DepartmentRepository;
import com.team01.hrbank.repository.EmployeeRepository;
import com.team01.hrbank.repository.custom.EmployeeQueryRepository;
import com.team01.hrbank.service.EmployeeService;
import com.team01.hrbank.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;

    private static final String EMPLOYEE = "직원";
    private static final String DEPARTMENT = "부서";

    @Override
    @Transactional
    public EmployeeDto save(EmployeeCreateRequest employeeCreateRequest, MultipartFile profile) throws IOException {
        if(employeeRepository.existsByEmail((employeeCreateRequest.email()))) {
            throw new DuplicateException(employeeCreateRequest.email());
        }

        Department department = departmentRepository.findById(employeeCreateRequest.departmentId())
            .orElseThrow(() -> new EntityNotFoundException(DEPARTMENT, employeeCreateRequest.departmentId()));

        BinaryContent binaryContent = null;
        if (profile != null && !profile.isEmpty()) {
            binaryContent = new BinaryContent(
                profile.getOriginalFilename(),
                (long) profile.getBytes().length,
                profile.getContentType()
            );
            binaryContent = binaryContentRepository.save(binaryContent);
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

        employeeRepository.save(employee);

        if (binaryContent != null) {
            binaryContentStorage.save(binaryContent.getId(), profile.getBytes());
        }

        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseEmployeeDto<EmployeeDto> findAll(
        String nameOrEmail, String employeeNumber, String departmentName,
        String position, LocalDate hireDateFrom, LocalDate hireDateTo,
        String status, String cursor, Long idAfter,
        int size, String sortField, String sortDirection
    ) {
        CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
            nameOrEmail, employeeNumber, departmentName,
            position, hireDateFrom, hireDateTo,
            EmployeeStatus.valueOf(status.toUpperCase()), idAfter ,cursor,
            size, sortField, sortDirection
        );

        return employeeRepository.findEmployeeByCursor(request);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        Employee employee = employeeRepository.findWithDetailsById(id)
            .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE, id));
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public EmployeeDto update(EmployeeUpdateRequest updateRequest, Long id, MultipartFile profile) throws IOException {

        Employee employee = employeeRepository.findWithDetailsById(id)
            .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE, id));

        if (employeeRepository.existsByEmailAndIdNot(updateRequest.email(), employee.getId())) {
            throw new DuplicateException(updateRequest.email());
        }

        Department department = departmentRepository.findById(updateRequest.departmentId())
            .orElseThrow(() -> new EntityNotFoundException(DEPARTMENT, updateRequest.departmentId()));

        BinaryContent binaryContent = null;
        if (profile != null && !profile.isEmpty()) {
            binaryContent = new BinaryContent(
                profile.getOriginalFilename(),
                (long) profile.getBytes().length,
                profile.getContentType()
            );
            binaryContent = binaryContentRepository.save(binaryContent);
        }

        employee.update(
            updateRequest.name(),
            updateRequest.email(),
            department,
            updateRequest.position(),
            updateRequest.hireDate(),
            EmployeeStatus.valueOf(updateRequest.status()),
            binaryContent
        );

        employeeRepository.save(employee);

        if (binaryContent != null) {
            System.out.println("binary" + binaryContent.getId());
            binaryContentStorage.save(binaryContent.getId(), profile.getBytes());
        }

        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE, id));
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {
        TimeUnit timeUnit = TimeUnit.from(unit);

        if (to == null) {
            to = LocalDate.now();
        }
        if (from == null) {
            from = calculateDefaultFrom(to, unit);
        }

        List<Object[]> rawResult = employeeRepository.countEmployeeTrend(from, to,
            timeUnit.name().toLowerCase());

        List<EmployeeTrendDto> trendList = new ArrayList<>();
        Long previousCount = null;

        for (Object[] row : rawResult) {
            LocalDate date = ((Instant) row[0]).atZone(ZoneId.systemDefault()).toLocalDate();
            Long count = ((Number) row[1]).longValue();

            Long change = (previousCount == null) ? null : count - previousCount;
            Double changeRate = (previousCount == null || previousCount == 0)
                ? null : (change * 100.0) / previousCount;

            trendList.add(new EmployeeTrendDto(date, count, change, changeRate));
            previousCount = count;
        }

        return trendList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status) {
        EmployeeStatus employeeStatus;
        String groupingKey;

        try {
            employeeStatus = EmployeeStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("유효하지 않은 상태 값입니다: " + status);
        }

        if (groupBy == null || (!groupBy.equalsIgnoreCase("department") && !groupBy.equalsIgnoreCase("position"))) {
            throw new IllegalStateException("유효하지 않은 그룹 기준입니다: " + groupBy);
        }

        groupingKey = groupBy.equalsIgnoreCase("position") ? "position" : "department";

        return employeeRepository.findDistributionBy(groupingKey, employeeStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Long employeeCount(String status, LocalDate fromDate, LocalDate toDate) {
        EmployeeStatus employeeStatus;

        try {
            employeeStatus = EmployeeStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("유효하지 않은 상태 값입니다: " + status);
        }

        return employeeRepository.employeeCountBy(employeeStatus, fromDate, toDate);
    }

    private LocalDate calculateDefaultFrom(LocalDate to, String unit) {
        return switch (unit.toLowerCase()) {
            case "day" -> to.minusDays(12);
            case "week" -> to.minusWeeks(12);
            case "quarter" -> to.minusMonths(12);
            case "year" -> to.minusYears(12);
            default -> to.minusMonths(12);
        };
    }
}
