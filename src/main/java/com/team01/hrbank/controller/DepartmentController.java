package com.team01.hrbank.controller;

import com.team01.hrbank.dto.department.DepartmentCreateRequest;
import com.team01.hrbank.dto.department.DepartmentDto;
import com.team01.hrbank.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(
        @Valid @RequestBody DepartmentCreateRequest request) {

        DepartmentDto response = departmentService.createDepartment(request);
        return ResponseEntity.ok(response); // 200 OK 응답
    }

}
