package com.team01.hrbank.service.binarycontent;

import com.team01.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.binarycontent.BinaryContentDto;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    @Transactional
    BinaryContentDto upload(BinaryContentCreateRequest request);

    List<BinaryContentDto> findAllByIdIn(List<Long> binaryContentIds);

    @Transactional(readOnly = true)
    BinaryContentDto find(Long binaryContentId);


    @Transactional
    void delete(Long binaryContentId);
}
