package com.team01.hrbank.storage;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

    Long put(Long id, byte[] bytes);

    Long putCsv(Long id, byte[] bytes);

    ResponseEntity<Resource> downloadResponse(BinaryContentDto binaryContentDto);
}