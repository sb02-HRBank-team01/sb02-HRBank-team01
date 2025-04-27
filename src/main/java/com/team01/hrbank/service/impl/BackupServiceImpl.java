package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.backup.BackupDto;
import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import com.team01.hrbank.entity.Backup;
import com.team01.hrbank.entity.BinaryContent;
import com.team01.hrbank.entity.Employee;
import com.team01.hrbank.enums.BackupStatus;
import com.team01.hrbank.mapper.BackupMapper;
import com.team01.hrbank.repository.BackupRepository;
import com.team01.hrbank.repository.EmployeeRepository;
import com.team01.hrbank.repository.ChangeLogRepository;
import com.team01.hrbank.service.BackupService;
import com.team01.hrbank.storage.CsvBackupStorage;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final BackupRepository backUpRepository;
    private final EmployeeRepository employeeRepository;
    private final CsvBackupStorage csvBackupStorage;
    private final BackupMapper backUpMapper;
    private final ChangeLogRepository changeLogRepository;

    @Transactional
    @Override
    @Scheduled(cron = "${spring.backup.schedule.time}")
    public void run() {
        try {
            final String worker = "system";
            if (!needLog()) {
                skippedBackup();
                return;
            }
            Backup runBackup = startBackup(worker);
            registerBackup(runBackup);
        } catch (IOException e) {
            throw new RuntimeException("백업 실행 중 IO 오류 발생", e);
        }
    }

    @Transactional
    @Override
    public BackupDto triggerManualBackup(String workerIp) {
        try {
            final String worker = workerIp;
            Backup initialBackup = startBackup(worker);
            registerBackup(initialBackup);
            Backup finalBackup = findBackupByIdOrThrow(initialBackup.getId());
            return backUpMapper.toDto(finalBackup);
        } catch (IOException e) {
            throw new RuntimeException("수동 백업 실행 중 IO 오류 발생", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Backup startBackup(String worker) {
        Backup newBackup = Backup.builder()
            .worker(worker)
            .startedAt(Instant.now())
            .status(BackupStatus.IN_PROGRESS)
            .build();
        return backUpRepository.save(newBackup);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void registerBackup(Backup backupInProgress) throws IOException {
        Long backupId = backupInProgress.getId();

//        // 테스트용: 실패 코드 - 백업 ID가 짝수인 경우 강제 실패
//        if (backupId != null && backupId % 2 == 0) {
//            handleBackupFailure(backupId,
//                new RuntimeException("registerBackup에서 짝수 ID 백업 강제 실패! Backup ID: " + backupId));
//            throw new RuntimeException("짝수 ID 백업 강제 실패!");
//        }
        
        List<Employee> employees;
        List<BinaryContent> profiles;
        
        employees = employeeRepository.findAll();
        profiles = employees.stream()
            .map(Employee::getProfile)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        Backup managed = findBackupByIdOrThrow(backupId);
        managed.setProFileBackup(profiles);
        backUpRepository.saveAndFlush(managed);

        try (Stream<Employee> stream = employees.stream()) {
            csvBackupStorage.saveCsvFromStream(backupId, stream);
        }

        Backup toComplete = findBackupByIdOrThrow(backupId);
        if (toComplete.getStatus() == BackupStatus.IN_PROGRESS) {
            toComplete.complete(Instant.now());
            backUpRepository.save(toComplete);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void handleBackupFailure(Long backupId, Exception cause) {
        try {
            csvBackupStorage.deleteCsvFile(backupId);
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 삭제 실패", e);
        }
        
        try {
            csvBackupStorage.saveErrorLog(backupId, cause);
        } catch (IOException e) {
            throw new RuntimeException("오류 로그 저장 실패", e);
        }
        
        Backup toFail = findBackupByIdOrThrow(backupId);
        toFail.fail(Instant.now());
        backUpRepository.saveAndFlush(toFail);
    }

    @Transactional(readOnly = true)
    @Override
    public BackupPageDto search(
        String worker,
        String statusStr,
        Instant startedAtFrom,
        Instant startedAtTo,
        CursorRequest req
    ) {
        BackupStatus status = parseStatus(statusStr);
        long totalElements = backUpRepository.countBackups(worker, status, startedAtFrom, startedAtTo);

        List<Backup> allResults = backUpRepository.searchWithCursor(
            worker, status, startedAtFrom, startedAtTo, req
        );

        boolean hasNext = allResults.size() > req.size();
        List<Backup> pageResults = hasNext ? allResults.subList(0, req.size()) : allResults;

        Long lastId = pageResults.isEmpty() ? null : pageResults.get(pageResults.size() - 1).getId();
        String nextCursor = generateCursor(lastId);

        return backUpMapper.toPageDto(
            pageResults,
            nextCursor,
            lastId,
            req.size(),
            totalElements,
            hasNext
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BackupDto> findLatestBackupByStatus(String statusStr) {
        BackupStatus status = parseStatus(statusStr);
        return backUpRepository.findTopByStatusOrderByStartedAtDesc(status)
            .map(backUpMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateBackupId(Long id) {
        Backup backup = findBackupByIdOrThrow(id);

        if (backup.getStatus() != BackupStatus.COMPLETED) {
            throw new IllegalStateException("백업이 완료되지 않음.");

        }
    }

    private boolean needLog() {
        final String batchWorker = "system";
        Optional<Backup> lastCompletedBatch = backUpRepository.findTopByWorkerAndStatusOrderByEndedAtDesc(
            batchWorker, BackupStatus.COMPLETED);

        if (lastCompletedBatch.isEmpty()) {
            return true;
        }

        Instant lastBatchEndTime = lastCompletedBatch.get().getEndedAt();
        if (lastBatchEndTime == null) {
            return true;
        }

        boolean changesExist = changeLogRepository.existsByCreatedAtAfter(lastBatchEndTime);
        return changesExist;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void skippedBackup() {
        Instant now = Instant.now();
        Backup skippedBackup = Backup.builder()
            .worker("system")
            .startedAt(now)
            .endedAt(now)
            .status(BackupStatus.SKIPPED)
            .build();
        backUpRepository.save(skippedBackup);
    }

    private BackupStatus parseStatus(String statusStr) {
        if (!StringUtils.hasText(statusStr)) {
            return null;
        }
        String upperStatus = statusStr.toUpperCase().trim();
        if ("COMPLETE".equals(upperStatus)) {
            return BackupStatus.COMPLETED;
        }
        try {
            return BackupStatus.valueOf(upperStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String generateCursor(Long lastId) {
        if (lastId == null) {
            return null;
        }
        String cursorData = String.format("{\"id\":%d}", lastId);
        return Base64.getEncoder().encodeToString(cursorData.getBytes());
    }

    private Backup findBackupByIdOrThrow(Long backupId) {
        return backUpRepository.findById(backupId)
            .orElseThrow(() -> new EntityNotFoundException("백업 ID에 해당하는 기록을 찾을 수 없습니다: " + backupId));
    }
}