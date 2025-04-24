package com.team01.hrbank.repository;

import com.team01.hrbank.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {

//    // 실험용입니다
//    @Query("SELECT bc.id as id, bc.fileName as fileName " +
//        "FROM BinaryContent bc " +
//        "WHERE EXISTS (SELECT 1 FROM Employee e WHERE e.profile = bc.id)")
//    List<BinaryContentInfo> findAllProfileId();
}