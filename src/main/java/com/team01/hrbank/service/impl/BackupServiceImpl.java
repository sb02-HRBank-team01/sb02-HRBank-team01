package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.backup.BackupDto;
import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.dto.backup.CursorRequest;
import com.team01.hrbank.entity.Backup;
import com.team01.hrbank.entity.BinaryContent;
import com.team01.hrbank.entity.Employee;
import com.team01.hrbank.enums.BackupStatus;
import com.team01.hrbank.exception.BackupFailedException;
import com.team01.hrbank.mapper.BackupMapper;
import com.team01.hrbank.repository.BackupRepository;
import com.team01.hrbank.repository.BinaryContentRepository;
import com.team01.hrbank.repository.ChangeLogRepository;
import com.team01.hrbank.repository.EmployeeRepository;
import com.team01.hrbank.service.BackupService;
import com.team01.hrbank.storage.BinaryContentStorage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final BackupRepository backupRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final EmployeeRepository employeeRepository;
    private final ChangeLogRepository changeLogRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BackupMapper backupMapper;

    @Scheduled(cron = "${spring.backup.schedule.time}")
    @Transactional
    public void scheduledBackup() {
        triggerManualBackup("SYSTEM");
    }

    @Transactional
    @Override
    public BackupDto triggerManualBackup(String requestIp) {
        Instant backupStartTime = Instant.now();

        // 1. IN_PROGRESS 상태로 먼저 저장
        Backup backup = Backup.builder()
            .status(BackupStatus.IN_PROGRESS)
            .worker(requestIp)
            .startedAt(backupStartTime)
            .build();
        backup = backupRepository.save(backup);

        try {
            // 2. 변경사항 확인
            Instant latestChangeLogCreatedAt = changeLogRepository.findLatestCreatedAt();
            Instant latestEmployeeCreatedAt = employeeRepository.findLatestCreatedAt();
            Optional<Backup> lastCompletedBackupOpt = backupRepository.findTopByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED);
            Instant lastBackupEndedAt = lastCompletedBackupOpt.map(Backup::getEndedAt).orElse(null);

            Instant latestChangeOrCreationTime = Stream.of(latestChangeLogCreatedAt, latestEmployeeCreatedAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

            boolean hasNoChangesSinceLastBackup = (latestChangeOrCreationTime == null)
                || (lastBackupEndedAt != null && latestChangeOrCreationTime.isBefore(lastBackupEndedAt));

            if (hasNoChangesSinceLastBackup) {
                // 3. 변경사항 없으면 SKIPPED 처리
                backup.setStatus(BackupStatus.SKIPPED);
                backup.setEndedAt(Instant.now());
                backupRepository.save(backup);

                return new BackupDto(
                    backup.getId(),
                    backup.getWorker(),
                    backup.getStartedAt(),
                    backup.getEndedAt(),
                    backup.getStatus().name(),
                    null
                );
            }

            // 4. 변경사항 있으면 CSV 파일 만들기
            List<Employee> employees = employeeRepository.findAll();
            byte[] csvData = createCsvData(employees);

            Long lastFileId = binaryContentRepository.findTopByOrderByIdDesc()
                .map(BinaryContent::getId)
                .orElse(0L);
            Long newFileId = lastFileId + 1;

            BinaryContent binaryContent = new BinaryContent(
                newFileId + ".csv",
                (long) csvData.length,
                "text/csv"
            );
            binaryContent = binaryContentRepository.save(binaryContent);

            // 파일 저장
            binaryContentStorage.putCsv(binaryContent.getId(), csvData);

            // 5. Backup 완료 처리
            backup.setStatus(BackupStatus.COMPLETED);
            backup.setFile(binaryContent);
            backup.setEndedAt(Instant.now());
            backupRepository.save(backup);

            return new BackupDto(
                backup.getId(),
                backup.getWorker(),
                backup.getStartedAt(),
                backup.getEndedAt(),
                backup.getStatus().name(),
                binaryContent.getId()
            );

        } catch (Exception e) {
            // 6. 실패 처리
            backup.setStatus(BackupStatus.FAILED);
            backup.setEndedAt(Instant.now());
            backupRepository.save(backup);

            return new BackupDto(
                backup.getId(),
                backup.getWorker(),
                backup.getStartedAt(),
                backup.getEndedAt(),
                backup.getStatus().name(),
                null
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BackupPageDto search(String worker, String status, Instant startedAtFrom, Instant startedAtTo, CursorRequest cursorRequest) {
        return backupRepository.search(worker, status, startedAtFrom, startedAtTo, cursorRequest);
    }

    @Override
    public BackupDto findLatestBackupByStatus(String statusStr) {
        return backupRepository.findTopByStatusOrderByEndedAtDesc(BackupStatus.valueOf(statusStr))
            .map(backupMapper::toDto)
            .orElse(null);
    }

    private byte[] createCsvData(List<Employee> employees) {
        // CSV 파일로 저장할 데이터를 변환하는 로직 구현
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,직원번호,이름,이메일,부서,직급,입사일,상태\n");

        for (Employee employee : employees) {
            csvBuilder.append(employee.getId()).append(",")
                .append(employee.getEmployeeNumber()).append(",")
                .append(employee.getName()).append(",")
                .append(employee.getEmail()).append(",")
                .append(employee.getDepartment().getName()).append(",")
                .append(employee.getPosition()).append(",")
                .append(employee.getHireDate()).append(",")
                .append(employee.getStatus().name()).append("\n");
        }

        return csvBuilder.toString().getBytes();
    }
}