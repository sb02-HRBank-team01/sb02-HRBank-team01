package com.team01.hrbank.service;

import com.team01.hrbank.dto.department.BinaryContent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.department.BinaryContent.BinaryContentDto;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    @Transactional
    BinaryContentDto upload(BinaryContentCreateRequest request);

    BinaryContentDto find(UUID binaryContentId);

    List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

    @Transactional
    void delete(UUID binaryContentId);
}
