package com.team01.hrbank.mapper;


import com.team01.hrbank.dto.backup.BackupDto;


import com.team01.hrbank.dto.backup.BackupPageDto;
import com.team01.hrbank.entity.Backup;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class BackupMapper {

    public BackupDto toDto(Backup backup) {
        String statusName = (backup.getStatus() != null)
            ? backup.getStatus().name()
            : null;

        return new BackupDto(
            backup.getId(),
            backup.getWorker(),
            backup.getStartedAt(),
            backup.getEndedAt(),
            statusName,
            backup.getId()
        );
    }

    public BackupPageDto toPageDto(
        List<Backup> backups,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
    ) {
        List<BackupDto> content = backups.stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        return new BackupPageDto(
            content,
            nextCursor,
            nextIdAfter,
            size,
            totalElements,
            hasNext
        );
    }
}
