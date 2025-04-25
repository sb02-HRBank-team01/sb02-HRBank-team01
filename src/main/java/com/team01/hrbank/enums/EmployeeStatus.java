package com.team01.hrbank.enums;

import java.util.Arrays;
import lombok.Getter;
@Getter
public enum EmployeeStatus {
  ACTIVE("재직중"),
  ON_LEAVE("휴직중"),
  RESIGNED("퇴사");

  private final String description;

  EmployeeStatus(String description) {
    this.description = description;
  }

  public static EmployeeStatus from(String description) {
    return Arrays.stream(EmployeeStatus.values())
        .filter(status -> status.getDescription().equals(description))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("상태를 찾을 수 없습니다: " + description));
  }

  @Override
  public String toString() {
    return this.description;
  }
}