package com.team01.hrbank.repository;


import com.team01.hrbank.entity.Backup;
import com.team01.hrbank.enums.BackupStatus;
import com.team01.hrbank.repository.custom.BackupCustomRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup,Long>, BackupCustomRepository {
    // worker와 완료 조건을 만족하는 가장 최근 종료된 백업 이력 조회(시간)
    Optional<Backup> findTopByWorkerAndStatusOrderByEndedAtDesc(String worker, BackupStatus status);

    //가장 최근에 시작된 특정 상태의 백업 이력 조회
    Optional<Backup> findTopByStatusOrderByStartedAtDesc(BackupStatus status);

    Optional<Backup> findTopByStatusOrderByEndedAtDesc(BackupStatus status);
}