package com.team01.hrbank.entity;

import com.team01.hrbank.enums.BackupStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Table(name = "back_ups")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Backup extends BaseEntity {

    @Column(name = "status", nullable = false, columnDefinition = "backup_status_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private BackupStatus status;

    @Column(name = "worker", nullable = false)
    private String worker;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "binary_content_id")
    private BinaryContent file;

    @Builder
    public Backup(Long id, Instant createdAt, BackupStatus status, String worker, Instant startedAt,
        Instant endedAt, BinaryContent file) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = (status != null) ? status : BackupStatus.IN_PROGRESS;
        this.worker = worker;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.file = file;
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
}
