package com.team01.hrbank.converter;

import com.team01.hrbank.constraint.EmployeeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = false)
public class EmployeeStatusConverter implements AttributeConverter<EmployeeStatus, String> {
  @Override
  public String convertToDatabaseColumn(EmployeeStatus status) {
    return status.getDescription();
  }

  @Override
  public EmployeeStatus convertToEntityAttribute(String dbData) {
    return EmployeeStatus.from(dbData);
  }
}
