package com.team01.hrbank.repository.custom;

import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import java.time.Instant;

public interface BackupCustomRepository {
    BackupPageDto search(String worker, String status, Instant startedAtFrom, Instant startedAtTo, CursorRequest cursorRequest);
}
