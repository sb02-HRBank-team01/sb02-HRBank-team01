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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupServiceImpl implements BackupService {

    private static final String BACKUP = "백업";
    private final BackupRepository backUpRepository;
    private final EmployeeRepository employeeRepository;
    private final CsvBackupStorage csvBackupStorage;
    private final BackupMapper backUpMapper;
    private final ChangeLogRepository changeLogRepository;

    @Transactional
    @Override
    @Scheduled(cron = "${backup.schedule.time}")
    public void run() throws IOException {
        final String worker = "system";
        if (!needLog()) {
            SkippedBackup();
            return;
        }
        Backup runBackup = runBackup(worker);
        registerBackup(runBackup);
    }

    @Transactional
    @Override
    public BackupDto triggerManualBackup(String workerIp) throws IOException {
        final String worker = workerIp;
        Backup initialBackup = runBackup(worker);
        registerBackup(initialBackup);
        Backup finalBackup = backUpRepository.findById(initialBackup.getId())
            .orElseThrow(() -> new IllegalStateException("아이디를 찾을 수 없습니다."));
        return backUpMapper.toDto(finalBackup);
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
        boolean hasNext;

        if (allResults.size() > req.size()) {
            hasNext = true;
        } else {
            hasNext = false;
        }

        List<Backup> pageResults;
        if (hasNext) {
            pageResults = allResults.subList(0, req.size());
        } else {
            pageResults = allResults;
        }

        Long lastId;
        if (pageResults.isEmpty()) {
            lastId = null;
        } else {
            lastId = pageResults.get(pageResults.size() - 1).getId();
        }


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
        if (status == null) {
            return Optional.empty();
        }
        return backUpRepository.findTopByStatusOrderByStartedAtDesc(status)
            .map(backUpMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateBackupId(Long id) {
        Backup backup = backUpRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("백업" + id));

        if (backup.getStatus() != BackupStatus.COMPLETED) {
            throw new IllegalStateException("백업" + backup.getStatus());
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void registerBackup(Backup backupInProgress) throws IOException {
        Long backupId = backupInProgress.getId();

        //n+1문제
        //fetch join 사용 시 해결 될 거로 보임 (즉시로딩)
        List<Employee> employees = employeeRepository.findAll();
        List<BinaryContent> profiles = employees.stream()
            .map(Employee::getProfile)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());


        Backup managed = backUpRepository.findById(backupId)
            .orElseThrow(() -> new EntityNotFoundException("백업 없음: " + backupId));
        managed.setProFileBackup(profiles);
        backUpRepository.save(managed);


        try (Stream<Employee> stream = employees.stream()) {
            csvBackupStorage.saveCsvFromStream(backupId, stream);
        }

        Backup toComplete = backUpRepository.findById(backupId)
            .orElseThrow(() -> new EntityNotFoundException("백업 없음: " + backupId));
        toComplete.complete(Instant.now());
        backUpRepository.save(toComplete);
    }


    private boolean needLog() {
        final String batchWorker = "system";
        // 가장 최근에 완료된 작업 이후 수정내역이 있다면
        Optional<Backup> lastCompletedBatch = backUpRepository.findTopByWorkerAndStatusOrderByEndedAtDesc(
            batchWorker, BackupStatus.COMPLETED);
        //없다면 무조건 한 개는 생성
        if (lastCompletedBatch.isEmpty()) {
            return true;
        }

        Instant lastBatchEndTime = lastCompletedBatch.get().getEndedAt();
        if (lastBatchEndTime == null) {
            throw new RuntimeException("서버 에러");
        }

        return changeLogRepository.existsByCreatedAtAfter(lastBatchEndTime);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Backup runBackup(String worker) {
        Backup newBackup = Backup.builder().worker(worker).startedAt(Instant.now())
            .status(BackupStatus.IN_PROGRESS).build();
        newBackup.start(newBackup.getStartedAt());
        return backUpRepository.save(newBackup);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void SkippedBackup() {
        Backup skippedBackup = Backup.builder().worker("system").startedAt(Instant.now())
            .endedAt(Instant.now()).status(BackupStatus.SKIPPED).build();
        backUpRepository.save(skippedBackup);
    }


    private BackupStatus parseStatus(String statusStr) {
        if (!StringUtils.hasText(statusStr)) {
            return null;
        }
        String upperStatus = statusStr.toUpperCase();
        if ("COMPLETE".equals(upperStatus)) {
            return BackupStatus.COMPLETED;
        }
        try {
            return BackupStatus.valueOf(upperStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    //인코딩해서 전달
    private String generateCursor(Long lastId) {
        if (lastId == null) {
            return null;
        }
        String jsonCursor = String.format("{\"id\":%d}", lastId);
        return Base64.getEncoder().encodeToString(jsonCursor.getBytes());
    }
}
