package com.team01.hrbank.service;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;

public interface BinaryContentService {


    BinaryContentDto find(Long binaryContentId);


    void delete(Long binaryContentId);


}
