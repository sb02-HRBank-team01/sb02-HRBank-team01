package com.team01.hrbank.enums;

import java.util.Arrays;

public enum TimeUnit {
  DAY,
  WEEK,
  MONTH,
  QUARTER,
  YEAR;

  public static TimeUnit from(String value) {
    return Arrays.stream(values())
        .filter(unit -> unit.name().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("지원하지 않는 시간 단위입니다: " + value));
  }
}