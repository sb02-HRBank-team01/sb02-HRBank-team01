package com.team01.hrbank.mapper.binarycontent;

import com.team01.hrbank.dto.binarycontent.BinaryContentDto;
import com.team01.hrbank.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper
public class BinaryContentMapper {

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(binaryContent.getId());
    }
}