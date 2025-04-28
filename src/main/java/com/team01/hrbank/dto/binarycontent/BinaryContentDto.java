package com.team01.hrbank.dto.binarycontent;

public record BinaryContentDto (
    Long id,
    String fileName,
    Long size,
    String contentType
) {

}
