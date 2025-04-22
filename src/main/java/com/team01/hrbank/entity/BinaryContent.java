package com.team01.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@Entity(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

    @Column(name = "file_name",nullable = false)
    private  String fileName;
    @Column(name = "size",nullable = false)
    private  Long size;
    @Column(name ="content_type",nullable = false)
    private  String contentType;


    public BinaryContent(String fileName, Long size, String contentType){
        this.fileName = fileName;
        this.size=size;
        this.contentType=contentType;
    }
}
