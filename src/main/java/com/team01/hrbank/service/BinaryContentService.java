package com.team01.hrbank.service;

import com.team01.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.binarycontent.BinaryContentDto;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface BinaryContentService {


    @Transactional(readOnly = true)
    BinaryContentDto find(Long binaryContentId);

    @Transactional
    void delete(Long binaryContentId);


}
