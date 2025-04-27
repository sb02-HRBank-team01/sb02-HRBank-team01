package com.team01.hrbank.controller;


import com.team01.hrbank.dto.backup.BackupDto;
import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import com.team01.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/backups")
@Slf4j
public class BackupController {

    private final BackupService backUpService;


    @GetMapping
    public ResponseEntity<BackupPageDto> findBackUpList(
        @RequestParam(value = "worker", required = false) String worker,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "startedAtFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAtFrom,
        @RequestParam(value = "startedAtTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAtTo,
        @RequestParam(value = "idAfter", required = false) Long idAfter, // 페이징용 ID
        @RequestParam(value = "cursor", required = false) String cursor,
        @RequestParam(value = "size", defaultValue = "10") int size,     // 페이지 크기
        @RequestParam(value = "sortField", defaultValue = "startedAt") String sortField, // 정렬 필드
        @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection // 정렬 방향
    ) {
        Sort.Direction dir = Direction.DESC;
        if (sortDirection.equalsIgnoreCase("ASC")) {
            dir = Direction.ASC;
        }

        CursorRequest req = new CursorRequest(idAfter, cursor, size, sortField, dir);
        BackupPageDto result = backUpService.search(worker, status, startedAtFrom, startedAtTo,
            req);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<BackupDto> createBackUp(HttpServletRequest request) throws IOException {
        String requesterIp = extractClientIp(request);
        BackupDto createdBackup = backUpService.triggerManualBackup(requesterIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBackup);
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> findLastestBackUp(
        @RequestParam(value = "status", required = false) String statusStr) {
        String statusToParse = (statusStr == null) ? "COMPLETED" : statusStr;
        return backUpService.findLatestBackupByStatus(statusToParse).map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-FORWARDED-FOR");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr;
    }
}
