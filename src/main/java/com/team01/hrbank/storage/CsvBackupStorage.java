package com.team01.hrbank.storage;

import com.team01.hrbank.entity.Employee;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface CsvBackupStorage {

    String saveCsvFromStream(Long backupId, Stream<Employee> employeeStream) throws IOException;

    String saveErrorLog(Long backupId, Exception error) throws IOException;

    InputStream load(String filename);


    InputStream get(Long id);

    ResponseEntity<Resource> downloadResponse(Long id);

    void deleteCsvFile(Long backupId) throws IOException;
}