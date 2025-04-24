package com.team01.hrbank.controller;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.service.BinaryContentService;
import com.team01.hrbank.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        BinaryContentDto binaryContentDto = binaryContentService.find(id);
        return binaryContentStorage.downloadResponse(binaryContentDto);
    }
}
