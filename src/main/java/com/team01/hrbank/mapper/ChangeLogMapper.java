package com.team01.hrbank.mapper;

import com.team01.hrbank.dto.changelog.ChangeLogDto;
import com.team01.hrbank.entity.ChangeLog;
import org.springframework.stereotype.Component;

@Component
public class ChangeLogMapper {
    public ChangeLogDto toDto(ChangeLog changeLog) {
        return new ChangeLogDto(
            changeLog.getId(),
            changeLog.getType(),
            changeLog.getEmployeeNumber(),
            changeLog.getMemo(),
            changeLog.getIpAddress(),
            changeLog.getUpdatedAt()
        );
    }
}
