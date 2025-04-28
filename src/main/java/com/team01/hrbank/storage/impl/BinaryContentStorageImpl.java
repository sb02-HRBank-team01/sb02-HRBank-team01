package com.team01.hrbank.storage.impl;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentStorageImpl implements BinaryContentStorage {

    private final Path root;

    private BinaryContentStorageImpl(@Value("${hr_bank.file}") Path root) {
        this.root = root;
    }

    @PostConstruct
    private void init() {
        try {
            if (!Files.exists(this.root)) {
                Files.createDirectories(this.root);
            }
        } catch (IOException e) {
            throw new RuntimeException("루트 디렉토리 초기화 실패", e);
        }
    }

    private Path resolvePath(Long id) {
        return root.resolve(id.toString());
    }

    @Override
    public Long put(Long id, byte[] bytes) {
        try (OutputStream os = Files.newOutputStream(resolvePath(id))) {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
        return id;
    }

    @Override
    public Long putCsv(Long id, byte[] bytes) {
        try (OutputStream os = Files.newOutputStream(resolvePath(id))) {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 저장 실패", e);
        }
        return id;
    }

    @Override
    public ResponseEntity<Resource> downloadResponse(BinaryContentDto binaryContentDto) {
        try {
            // fileName 대신 id를 이용해서 찾는다
            Long id = binaryContentDto.id();
            Path filePath = resolvePath(id);

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new PathResource(filePath);
            String contentType = binaryContentDto.contentType();

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(Files.size(filePath))
                .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드 실패", e);
        }
    }
}