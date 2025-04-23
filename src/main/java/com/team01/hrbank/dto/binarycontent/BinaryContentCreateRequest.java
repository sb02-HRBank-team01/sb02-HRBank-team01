package com.team01.hrbank.dto.binarycontent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일이름은 필수입니다.")
    String fileName,
    @NotBlank(message = "파일 콘텐트 타입은 필수입니다.")
    String contentType,
    @NotNull(message = "파일 데이터는 필수입니다.")
    @Size(min = 1, message = "파일 데이터는 비어 있을 수 없습니다.")
    byte[] bytes
) {}