package com.team01.hrbank.controller;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.service.BackupService;
import com.team01.hrbank.storage.CsvBackupStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class BinaryContentController {

    private final CsvBackupStorage csvBackupStorage;
    private final BackupService backupService;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        backupService.validateBackupId(id);
        return csvBackupStorage.downloadResponse(id);
    }
}
