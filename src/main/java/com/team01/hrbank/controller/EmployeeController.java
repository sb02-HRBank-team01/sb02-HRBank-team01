package com.team01.hrbank.controller;

import com.team01.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeCreateRequest;
import com.team01.hrbank.dto.employee.EmployeeDistributionDto;
import com.team01.hrbank.dto.employee.EmployeeDto;
import com.team01.hrbank.dto.employee.EmployeeTrendDto;
import com.team01.hrbank.dto.employee.EmployeeUpdateRequest;
import com.team01.hrbank.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<CursorPageResponseEmployeeDto<EmployeeDto>> findAll(
        @RequestParam(required = false) String nameOrEmail,
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) String departmentName,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateTo,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        CursorPageResponseEmployeeDto<EmployeeDto> response = employeeService.findAll(
            nameOrEmail, employeeNumber, departmentName, position,
            hireDateFrom, hireDateTo, status,
            cursor, idAfter, size, sortField, sortDirection
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EmployeeDto> save(
        @Valid @RequestPart("employee") EmployeeCreateRequest employeeCreateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile,
        HttpServletRequest servletRequest
    ) throws IOException {
        EmployeeDto employeeDto = employeeService.save(employeeCreateRequest, profile, servletRequest);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findById(
        @PathVariable Long id
    ) {
        EmployeeDto employeeDto = employeeService.findById(id);
        return ResponseEntity.ok(employeeDto);
    }

    @DeleteMapping("{id}")
    public  ResponseEntity<EmployeeDto> delete(
        @PathVariable Long id,
        HttpServletRequest servletRequest
    ) {
        employeeService.delete(id, servletRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(
        path = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EmployeeDto> update(
        @PathVariable Long id,
        @Valid @RequestPart("employee") EmployeeUpdateRequest updateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile,
        HttpServletRequest servletRequest
    ) throws IOException {
        EmployeeDto employeeDto = employeeService.update(updateRequest, id, profile, servletRequest);
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> findEmployeeTrend(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(defaultValue = "month") String unit
    ) {
        return ResponseEntity.ok(employeeService.getEmployeeTrend(from, to, unit));
    }

    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> findEmployeeDistribution(
        @RequestParam(defaultValue = "department") String groupBy,
        @RequestParam(defaultValue = "ACTIVE") String status
    ) {
        return ResponseEntity.ok(employeeService.getEmployeeDistribution(groupBy, status));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> findEmployeeCount(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(employeeService.employeeCount(status, fromDate, toDate));
    }
}
