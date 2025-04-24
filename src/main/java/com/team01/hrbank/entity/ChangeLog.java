package com.team01.hrbank.entity;

import com.team01.hrbank.enums.ChangeType;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class ChangeLog extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeType type; // CREATED, UPDATED, DELETED

    @Column(nullable = false)
    private String employeeNumber; // 사번

    @Column(length = 500)
    private String memo;

    @Column(nullable = false)
    private String ipAddress;

    // `updatedAt`을 Swagger의 "at" 필드로 사용 (BaseUpdatableEntity에서 상속됨)

    @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeLogDetail> details = new ArrayList<>();

    public ChangeLog(ChangeType type, String employeeNumber, String memo, String ipAddress) {
        this.type = type;
        this.employeeNumber = employeeNumber;
        this.memo = memo;
        this.ipAddress = ipAddress;
    }

    public void addDetail(ChangeLogDetail detail) {
        this.details.add(detail);
        detail.setChangeLog(this);
    }

    // BaseUpdatableEntity에 Getter를 추가하는 대신 여기에 updatedAt get함수를 추가
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
