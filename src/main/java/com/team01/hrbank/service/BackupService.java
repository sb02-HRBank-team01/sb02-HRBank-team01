package com.team01.hrbank.service;


import com.team01.hrbank.dto.backup.BackupDto;
import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface BackupService {

    BackupDto triggerManualBackup(String requesterIp);

    BackupPageDto search(String worker, String statusStr, Instant startedAtFrom,
        Instant startedAtTo, CursorRequest req);

    BackupDto findLatestBackupByStatus(String statusStr);
}
