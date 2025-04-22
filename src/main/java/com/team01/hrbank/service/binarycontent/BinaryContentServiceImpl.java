package com.team01.hrbank.service;

import com.team01.hrbank.dto.department.BinaryContent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.department.BinaryContent.BinaryContentDto;
import com.team01.hrbank.entity.BinaryContent;
import com.team01.hrbank.mapper.BinaryContentMapper;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

public class BinaryContentServiceImpl implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDto upload(BinaryContentCreateRequest request) {
        String fileName = request.fileName();
        byte[] bytes = request.bytes();
        String contentType = request.contentType();
        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.save(binaryContent.getId(), bytes);

        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId).map(binaryContentMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("해당 사원의 아이디가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException(
                "BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
