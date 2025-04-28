package com.team01.hrbank.mapper;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    public BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
            binaryContent.getId(),
            binaryContent.getFileName(),
            binaryContent.getSize(),
            binaryContent.getContentType()
        );
    }
}
