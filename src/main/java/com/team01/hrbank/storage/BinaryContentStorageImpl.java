package com.team01.hrbank.storage;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.exception.DuplicateException;
import com.team01.hrbank.exception.FileOperationException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
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
    // 기본값으로 설정시킴 설정함 야물파일 설정 x
    public BinaryContentStorageImpl(
        @Value("${binary.content.root:profiles}") String rootDir
    ) {
        this.root = Paths.get(rootDir)
            .toAbsolutePath()
            .normalize();
    }
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new FileOperationException("파일을 생성할 수 없습니다.");
        }
    }

    @Override
    public Long save(Long id, byte[] bytes) {
        Path file = resolvePath(id);
        if (Files.exists(file)) {
            throw new DuplicateException("파일이 이미 존재합니다.");
        }
        try {
            Files.write(file, bytes);
            return id;
        } catch (IOException e) {
            throw new FileOperationException("파일에 쓸 수 없습니다.");
        }
    }

    @Override
    public InputStream get(Long id) {
        Path file = resolvePath(id);
        if (Files.notExists(file)) {
            throw new FileOperationException("파일을 찾을 수 없습니다.");
        }
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new FileOperationException("파일을 읽을 수 없습니다.");
        }
    }

    private Path resolvePath(Long id) {
        return root.resolve(id.toString());
    }


}
