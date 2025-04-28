package com.team01.hrbank.entity;

import com.team01.hrbank.enums.ChangeType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(name = "change_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class ChangeLog extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false, columnDefinition = "change_type")
    private ChangeType type; // CREATED, UPDATED, DELETED

    @Column(name = "employee_number", nullable = false)
    private String employeeNumber;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeLogDetail> details = new ArrayList<>();

    public ChangeLog(ChangeType type, String employeeNumber, String memo, String ipAddress) {
        this.type = type;
        this.employeeNumber = employeeNumber;
        this.memo = memo;
        this.ipAddress = ipAddress;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
