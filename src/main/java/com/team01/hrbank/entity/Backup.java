package com.team01.hrbank.entity;



import com.team01.hrbank.enums.BackupStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AccessLevel;

@Entity
@Table(name = "back_ups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "empProfiles")
public class Backup extends BaseUpdatableEntity {

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BackupStatus status;

    @Column(name = "worker", nullable = false)
    private String worker;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "backup_files",
        joinColumns = @JoinColumn(name = "backup_id"),
        inverseJoinColumns = @JoinColumn(name = "binary_content_id"))
    private List<BinaryContent> empProfiles = new ArrayList<>();

    @Builder
    public Backup(Long id, Instant createdAt, Instant updatedAt, BackupStatus status, String worker, Instant startedAt, Instant endedAt, List<BinaryContent> employeeProfilePicturesAtBackup) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = (status != null) ? status : BackupStatus.IN_PROGRESS;
        this.worker = worker;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.empProfiles = new ArrayList<>();
    }

    public void start(Instant startTime) {
        if (this.status == BackupStatus.COMPLETED || this.status == BackupStatus.FAILED) {
            throw new IllegalStateException("백업을 시작할 수 없습니다.");
        }
        if (this.startedAt == null) {
            this.startedAt = startTime;
        }
        this.status = BackupStatus.IN_PROGRESS;
    }

    public void complete(Instant endTime) {
        if (this.status != BackupStatus.IN_PROGRESS) {
            throw new IllegalStateException("백업을 완료할 수 없습니다.");
        }
        this.endedAt = endTime;
        this.status = BackupStatus.COMPLETED;
    }

    public void fail(Instant endTime) {
        if (this.status != BackupStatus.IN_PROGRESS) {
            throw new IllegalStateException("오류발생");
        }
        this.endedAt = endTime;
        this.status = BackupStatus.FAILED;
    }

    public void setProFileBackup(List<BinaryContent> profile) {
        this.empProfiles = profile;
    }
}