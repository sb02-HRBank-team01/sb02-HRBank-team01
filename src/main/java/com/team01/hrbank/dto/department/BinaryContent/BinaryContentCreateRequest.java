package com.team01.hrbank.dto.department.BinaryContent;

public record BinaryContentCreateRequest(String fileName, String contentType, byte[] bytes) {


}