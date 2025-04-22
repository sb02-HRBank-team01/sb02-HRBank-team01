package com.team01.hrbank.mapper;

import com.team01.hrbank.dto.BinaryContent.BinaryContentDto;
import com.team01.hrbank.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(binaryContent.getId());
    }
}