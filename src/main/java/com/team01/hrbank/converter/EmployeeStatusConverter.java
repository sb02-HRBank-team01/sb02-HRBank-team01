package com.team01.hrbank.converter;

import com.team01.hrbank.constraint.EmployeeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = true)
public class EmployeeStatusConverter implements AttributeConverter<EmployeeStatus, String> {
  @Override
  public String convertToDatabaseColumn(EmployeeStatus status) {
    return status.getDescription(); // 한글 저장
  }

  @Override
  public EmployeeStatus convertToEntityAttribute(String dbData) {
    return Arrays.stream(EmployeeStatus.values())
        .filter(e -> e.getDescription().equals(dbData))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + dbData));
  }
}
