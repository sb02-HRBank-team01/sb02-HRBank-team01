package com.team01.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "change_log_id", nullable = false)
    private ChangeLog changeLog;

    @Column(nullable = false)
    private String propertyName;

    @Column(columnDefinition = "TEXT")
    private String before;

    @Column(columnDefinition = "TEXT")
    private String after;

    public ChangeLogDetail(ChangeLog changeLog, String propertyName, String before, String after) {
        this.changeLog = changeLog;
        this.propertyName = propertyName;
        this.before = before;
        this.after = after;
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }
}
