package com.team01.hrbank.service.impl;


import com.team01.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.dto.binarycontent.BinaryContentInfo;
import com.team01.hrbank.entity.BinaryContent;

import com.team01.hrbank.exception.MismatchedValueException;
import com.team01.hrbank.mapper.BinaryContentMapper;
import com.team01.hrbank.repository.BinaryContentRepository;
import com.team01.hrbank.storage.BinaryContentStorage;
import com.team01.hrbank.service.BinaryContentService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
        Long id = binaryContentStorage.save(binaryContent.getId(), bytes);

        if (id == null || !id.equals(binaryContent.getId())) {
            throw new MismatchedValueException("아이디가 일치하지 않습니다");
        }
        return binaryContentMapper.toDto(binaryContent);
    }

    @Transactional(readOnly = true)
    @Override
    public BinaryContentDto find(Long binaryContentId) {
        return binaryContentRepository.findById(binaryContentId).map(binaryContentMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("프로필 아이디가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BinaryContentDto> findAllByIdIn(List<Long> binaryContentIds) {
        return binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void delete(Long binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("요청한 프로필 사진이 존재하지 않습니다");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }

//    @Transactional(readOnly = true)
//    @Override
//    public void streamProfileDownload(OutputStream outputStream) throws IOException {
//
//        List<BinaryContentInfo> allPictures = binaryContentRepository.findAllProfileId();
//
//        if (allPictures.isEmpty()) {
//
//            return;
//        }
//
//        // zos.close 자동
//        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
//            byte[] buffer = new byte[4096]; // 파일 내용 복사 시 사용할 임시 버퍼
//
//
//            for (BinaryContentInfo picture : allPictures) {
//                String originalFileName = picture.name();
//                Long fileId = picture.id();
//
//                // 원본 파일명이 없을 경우
//                if (originalFileName == null || originalFileName.isBlank()) {
//                    originalFileName = "file_" + fileId + ".bin";
//                }
//
//                //원본 이름으로 대체
//                ZipEntry zipEntry = new ZipEntry(originalFileName);
//
//                try {
//                    // 파일 저장소에서 ID를 이용해 실제 파일 내용 가져오기
//                    try (InputStream fileInputStream = binaryContentStorage.get(fileId)) {
//
//                        zos.putNextEntry(zipEntry);
//                        int length;
//                        // 파일 내용을 버퍼만큼 읽어와서 ZIP 스트림에 쓰기 반복
//                        while ((length = fileInputStream.read(buffer)) > 0) {
//                            zos.write(buffer, 0, length);
//                        }
//                        zos.closeEntry();
//                    }
//                    //오류 있어도 계속 진행
//                } catch (Exception e) {
//                    System.err.println(
//                        "오류: 파일 처리 실패 (ID: " + fileId + ", Name: " + originalFileName + ") - "
//                            + e.getMessage());
//                }
//            }
//
//        }
//
//    }
}


