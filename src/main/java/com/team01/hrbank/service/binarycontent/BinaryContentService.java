package com.team01.hrbank.service.binarycontent;

import com.team01.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface BinaryContentService {

    @Transactional
    Long upload(BinaryContentCreateRequest request);


    @Transactional(readOnly = true)
    BinaryContentDto find(Long binaryContentId);

    @Transactional(readOnly = true)
    List<BinaryContentDto> findAllByIdIn(List<Long> binaryContentIds);

    @Transactional
    void delete(Long binaryContentId);
}
