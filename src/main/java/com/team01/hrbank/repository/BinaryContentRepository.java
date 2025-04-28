package com.team01.hrbank.repository;

import com.team01.hrbank.entity.BinaryContent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
    Optional<BinaryContent> findTopByOrderByIdDesc();
}