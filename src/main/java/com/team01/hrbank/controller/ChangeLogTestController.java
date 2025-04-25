package com.team01.hrbank.controller;

import com.team01.hrbank.dto.changelog.DiffDto;
import com.team01.hrbank.enums.ChangeType;
import com.team01.hrbank.service.ChangeLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//test
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogTestController {

    private final ChangeLogService changeLogService;

    @PostMapping("/test-insert")
    public ResponseEntity<Void> insertTestLog() {
        // 샘플 DiffDto
        List<DiffDto> diffs = List.of(
            new DiffDto("직책", "사원", "대리"),
            new DiffDto("부서", "영업1팀", "영업2팀")
        );

        changeLogService.save(
            ChangeType.UPDATED,              // 이력 타입
            "230103",                        // 사번
            diffs,                           // 변경 필드
            "Postman 테스트 로그입니다.",     // 메모
            "127.0.0.1"                      // IP 주소
        );

        return ResponseEntity.ok().build();
    }
}

