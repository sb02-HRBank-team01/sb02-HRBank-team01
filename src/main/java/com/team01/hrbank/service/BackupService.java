package com.team01.hrbank.service;


import com.team01.hrbank.dto.backup.BackupDto;
import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;

public interface BackupService {


    @Scheduled(cron = "${spring.backup.schedule.time}")
    void run();

    BackupDto triggerManualBackup(String requesterIp) throws IOException;


    BackupPageDto search(String worker, String statusStr, Instant startedAtFrom,
        Instant startedAtTo, CursorRequest req);

    Optional<BackupDto> findLatestBackupByStatus(String statusStr);

    void validateBackupId(Long id);
}
