package com.team01.hrbank.repository;

import com.team01.hrbank.entity.ChangeLogDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogDetailRepository extends JpaRepository<ChangeLogDetail, Long> {
    List<ChangeLogDetail> findAllByChangeLogId(Long changeLogId);
}
