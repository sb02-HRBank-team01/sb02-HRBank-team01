//package com.team01.hrbank.controller;
//
//import com.team01.hrbank.dto.changelog.ChangeLogDto;
//import com.team01.hrbank.entity.ChangeLog;
//import com.team01.hrbank.enums.ChangeType;
//import com.team01.hrbank.mapper.ChangeLogMapper;
//import com.team01.hrbank.repository.ChangeLogRepository;
//import jakarta.transaction.Transactional;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/test/changelog")
//@RequiredArgsConstructor
//public class ChangeLogController {
//
//    private final ChangeLogRepository changeLogRepository;
//    private final ChangeLogMapper changeLogMapper;
//
//    @PostMapping("/sample")
//    public ResponseEntity<Void> saveSample() {
//        ChangeLog log = new ChangeLog(
//            ChangeType.CREATED,
//            "EMP-2024-001",
//            "Postman 테스트용",
//            "127.0.0.1"
//        );
//        changeLogRepository.save(log);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/list")
//    public ResponseEntity<List<ChangeLogDto>> list() {
//        List<ChangeLog> logs = changeLogRepository.findAll();
//        List<ChangeLogDto> result = logs.stream()
//            .map(changeLogMapper::toDto)
//            .toList();
//        return ResponseEntity.ok(result);
//    }
//}
