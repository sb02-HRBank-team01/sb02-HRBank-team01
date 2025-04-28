package com.team01.hrbank.service.impl;

import com.team01.hrbank.dto.changelog.ChangeLogDto;
import com.team01.hrbank.dto.changelog.CursorPageResponseChangeLogDto;
import com.team01.hrbank.dto.changelog.DiffDto;
import com.team01.hrbank.entity.ChangeLog;
import com.team01.hrbank.entity.ChangeLogDetail;
import com.team01.hrbank.enums.ChangeType;
import com.team01.hrbank.exception.EntityNotFoundException;
import com.team01.hrbank.mapper.ChangeLogMapper;
import com.team01.hrbank.repository.ChangeLogDetailRepository;
import com.team01.hrbank.repository.ChangeLogRepository;
import com.team01.hrbank.service.ChangeLogService;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;
    private final ChangeLogDetailRepository changeLogDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseChangeLogDto searchChangeLogs(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo,
        Long idAfter,
        String sortField,
        String sortDirection,
        int size
    ) {
        // 1. 커서 값 검증 (idAfter 유효값 검증)
        if(idAfter != null && changeLogRepository.existsById(idAfter)){
            throw new IllegalArgumentException("유효하지 않은 커서 값입니다.: idAfter = " + idAfter);
        }

        // 2. 데이터 조건 검색 (size + 1로 조회해 hasNext 판단)
        List<ChangeLog> logs = changeLogRepository.findByConditions(
            employeeNumber, type, memo, ipAddress,
            atFrom, atTo, idAfter,
            sortField, sortDirection,
            size + 1
        );

        // 3. 다음 페이지 존재 여부 판단
        boolean hasNext = logs.size() > size;
        if (hasNext) {
            logs = logs.subList(0, size);
        }

        // 4. Entity -> DTO 변환
        List<ChangeLogDto> dtos = logs.stream()
            .map(changeLogMapper::toDto)
            .collect(Collectors.toList());

        // 5. 다음 커서(ID) 설정
        Long nextIdAfter = hasNext ? logs.get(logs.size() - 1).getId() : null;
        String nextCursor = nextIdAfter != null ? String.valueOf(nextIdAfter) : null;

        // 6. 조건 기반 전체 개수 조회
        long totalElements = changeLogRepository.countByConditions(
            employeeNumber, type, memo, ipAddress, atFrom, atTo
        );

        // 7. 최종 응답 DTO 구성
        return new CursorPageResponseChangeLogDto(
            dtos,
            nextCursor,
            nextIdAfter,
            size,
            totalElements,
            hasNext
        );
    }

    @Override
    @Transactional
    public void save(ChangeType type, String employeeNumber, List<DiffDto> details, String memo, String ipAddress) {
        ChangeLog changeLog = new ChangeLog(type, employeeNumber, memo, ipAddress);
        changeLogRepository.save(changeLog);

        List<ChangeLogDetail> detailEntities = details.stream()
            .map(diffDto -> new ChangeLogDetail(
                changeLog,
                diffDto.propertyName(),
                diffDto.before(),
                diffDto.after()
            ))
            .toList();

        changeLogDetailRepository.saveAll(detailEntities);
    }


    @Override
    @Transactional(readOnly = true)
    public List<DiffDto> findChangeDetails(Long changeLogId) {
        boolean exists = changeLogRepository.existsById(changeLogId);
        if (!exists) {
            throw new EntityNotFoundException("ChangeLog", changeLogId);
        }
        List<ChangeLogDetail> details = changeLogDetailRepository.findAllByChangeLogId(changeLogId);

        return details.stream()
            .map(detail -> new DiffDto(
                detail.getPropertyName(),
                detail.getBefore(),
                detail.getAfter()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countChangeLogs(
        String employeeNumber,
        ChangeType type,
        String memo,
        String ipAddress,
        Instant atFrom,
        Instant atTo
    ){
        return changeLogRepository.countByConditions(
            employeeNumber,
            type,
            memo,
            ipAddress,
            atFrom,
            atTo
        );
    }
}