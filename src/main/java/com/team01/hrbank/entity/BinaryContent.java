package com.team01.hrbank.entity;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "binary_contents")
@Entity
@Getter
@NoArgsConstructor
public class BinaryContent extends BaseEntity{


    @Column(name = "file_name",nullable = false)
    String fileName;
    @Column(name = "size",nullable = false)
    Long size;
    @Column(name ="content_type",nullable = false)
    String contentType;


    public BinaryContent(String fileName,Long size,String contentType){
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }


}
