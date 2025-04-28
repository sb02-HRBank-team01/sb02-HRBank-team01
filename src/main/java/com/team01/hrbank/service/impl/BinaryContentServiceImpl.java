package com.team01.hrbank.service.impl;


import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.entity.BinaryContent;
import com.team01.hrbank.mapper.BinaryContentMapper;
import com.team01.hrbank.repository.BinaryContentRepository;
import com.team01.hrbank.service.BinaryContentService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(Long binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("BinaryContent not found"));
        return binaryContentMapper.toDto(binaryContent);
    }
}


