package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.changelog.DiffDto;
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
import com.team01.hrbank.enums.ChangeType;
import com.team01.hrbank.enums.EmployeeStatus;
import com.team01.hrbank.exception.DuplicateException;
import com.team01.hrbank.exception.EntityNotFoundException;
import com.team01.hrbank.mapper.EmployeeMapper;
import com.team01.hrbank.repository.BinaryContentRepository;
import com.team01.hrbank.repository.DepartmentRepository;
import com.team01.hrbank.repository.EmployeeRepository;
import com.team01.hrbank.service.ChangeLogService;
import com.team01.hrbank.service.EmployeeService;
import com.team01.hrbank.storage.BinaryContentStorage;
import com.team01.hrbank.util.DiffUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    private final ChangeLogService changeLogService;

    private static final String EMPLOYEE = "직원";
    private static final String DEPARTMENT = "부서";

    @Override
    @Transactional
    public EmployeeDto save(EmployeeCreateRequest employeeCreateRequest, MultipartFile profile, HttpServletRequest request) throws IOException {
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
            generateEmployeeNumber(),
            department,
            employeeCreateRequest.position(),
            employeeCreateRequest.hireDate(),
            EmployeeStatus.ACTIVE,
            binaryContent
        );

        employeeRepository.save(employee);

        // Client IP 가져오기
        String ipAddress = getClientIp(request);

        // 직원 저장 후 changelog등록
        changeLogService.save(
            ChangeType.CREATED,
            employee.getEmployeeNumber(),
            // DiffDto는 변경 시 필요 -> 빈 리스트
            List.of(),
            employeeCreateRequest.memo(),
            ipAddress
        );

        if (binaryContent != null) {
            binaryContentStorage.put(binaryContent.getId(), profile.getBytes());
        }

        return employeeMapper.toDto(employee);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseEmployeeDto<EmployeeDto> findAll(
        String nameOrEmail, String employeeNumber, String departmentName,
        String position, LocalDate hireDateFrom, LocalDate hireDateTo,
        String status, String cursor, Long idAfter,
        int size, String sortField, String sortDirection
    ) {
        EmployeeStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            statusEnum = EmployeeStatus.valueOf(status.toUpperCase());
        }

        CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
            nameOrEmail, employeeNumber, departmentName,
            position, hireDateFrom, hireDateTo,
            statusEnum, idAfter ,cursor,
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
    public EmployeeDto update(EmployeeUpdateRequest updateRequest, Long id, MultipartFile profile, HttpServletRequest request) throws IOException {

        Employee employee = employeeRepository.findWithDetailsById(id)
            .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE, id));

        if (employeeRepository.existsByEmailAndIdNot(updateRequest.email(), employee.getId())) {
            throw new DuplicateException(updateRequest.email());
        }

        Department department = departmentRepository.findById(updateRequest.departmentId())
            .orElseThrow(() -> new EntityNotFoundException(DEPARTMENT, updateRequest.departmentId()));

        BinaryContent binaryContent = employee.getProfile();

        if (profile != null && !profile.isEmpty()) {
            binaryContent = new BinaryContent(
                profile.getOriginalFilename(),
                (long) profile.getBytes().length,
                profile.getContentType()
            );
            binaryContent = binaryContentRepository.save(binaryContent);
        }

        // changeLog
        // 수정 전/후 비교 후 변경 사항(diff) 기록
        List<DiffDto> diffs = DiffUtil.compare(employee, updateRequest);

        // Client IP 가져오기
        String ipAddress = getClientIp(request);

        // 직원 저장 후 changelog등록
        changeLogService.save(
            ChangeType.UPDATED,
            employee.getEmployeeNumber(),
            // DiffDto는 변경 시 필요 -> 빈 리스트
            diffs,
            updateRequest.memo(),
            ipAddress
        );

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

        if (profile != null && !profile.isEmpty()) {
            binaryContentStorage.put(binaryContent.getId(), profile.getBytes());
        }

        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public void delete(Long id, HttpServletRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE, id));

        // Client IP 가져오기
        String ipAddress = getClientIp(request);

        // changelog
        changeLogService.save(
            ChangeType.DELETED,
            employee.getEmployeeNumber(),
            List.of(),
            "직원 삭제",
            ipAddress
        );



        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {
        LocalDate now = LocalDate.now();
        if (to == null) {
            to = now;
        }
        if (from == null) {
            from = calculateDefaultFrom(to, unit);
        }

        return employeeRepository.findEmployeeTrend(from, to, unit);
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
        EmployeeStatus employeeStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                employeeStatus = EmployeeStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("유효하지 않은 상태 값입니다: " + status);
            }
        }
        return employeeRepository.employeeCountBy(employeeStatus, fromDate, toDate);
    }

    private String generateEmployeeNumber() {
        Employee lastEmployee = employeeRepository.findTopByOrderByIdDesc().orElse(null);
        Long newEmployeeId = (lastEmployee != null) ? lastEmployee.getId() + 1 : 1L;
        return String.format("EMP%05d", newEmployeeId);
    }

    private LocalDate calculateDefaultFrom(LocalDate to, String unit) {
        return switch (unit.toLowerCase()) {
            case "day" -> to.minusDays(12);
            case "week" -> to.minusWeeks(12);
            case "quarter" -> to.minusMonths(12 * 3); // 12분기 = 3년
            case "year" -> to.minusYears(12);
            default -> to.minusMonths(12); // month 기본값
        };
    }
}
