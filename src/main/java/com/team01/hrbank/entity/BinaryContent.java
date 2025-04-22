package com.team01.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
@Table(name = "binary_contents")
@Entity(name = "binary_contents")
public class BinaryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "file_name",nullable = false)
    String fileName;
    @Column(name = "size",nullable = false)
    Long size;
    @Column(name ="content_type",nullable = false)
    String contentType;
    @Column(name="create_at",nullable = false)
    LocalDateTime createAt;


}
