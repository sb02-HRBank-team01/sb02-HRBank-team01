package com.team01.hrbank.repository.binarycontent;

import com.team01.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.repository.binarycontentStorage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentStorageImpl implements BinaryContentStorage {

    private final Path root;

    public BinaryContentStorageImpl(
        @Value("${binary.content.root}") String rootDir
    ) {
        this.root = Paths.get(rootDir)
            .toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("파일 생성 오류 " );
        }
    }

    @Override
    public Long save(Long id, byte[] bytes) {
        Path file = resolvePath(id);
        if (Files.exists(file)) {
            throw new IllegalArgumentException("파일이 이미 존재함");
        }
        try {
            Files.write(file, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 오류: " + file, e);
        }
    }

    @Override
    public InputStream get(Long id) {
        Path file = resolvePath(id);
        if (Files.notExists(file)) {
            throw new NoSuchElementException("파일을 찾을 수 없음");
        }
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는데 실패함");
        }
    }

    @Override
    public ResponseEntity<Resource> downloadResponse(BinaryContentDto dto) {

        Resource resource = new InputStreamResource(dto.id() == null ? null : get(dto.id()));

        String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
    private Path resolvePath(Long id) {
        return root.resolve(id.toString());
    }
}
