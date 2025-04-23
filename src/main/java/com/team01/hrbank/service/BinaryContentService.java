package com.team01.hrbank.service;

import com.team01.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.binarycontent.BinaryContentDto;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface BinaryContentService {

    @Transactional
    BinaryContentDto upload(BinaryContentCreateRequest request);

    List<BinaryContentDto> findAllByIdIn(List<Long> binaryContentIds);

    @Transactional(readOnly = true)
    BinaryContentDto find(Long binaryContentId);


    @Transactional
    void delete(Long binaryContentId);

//
//    @Transactional(readOnly = true)
//    void streamProfileDownload(OutputStream outputStream) throws IOException;
}
