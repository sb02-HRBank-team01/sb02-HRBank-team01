package com.team01.hrbank.dto.binarycontent;

public record BinaryContentCreateRequest(String fileName, String contentType, byte[] bytes) {


}