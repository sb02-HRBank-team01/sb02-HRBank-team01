package com.team01.hrbank.storage;


import com.team01.hrbank.entity.Employee;
import com.team01.hrbank.exception.FileOperationException;
import jakarta.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CsvBackupStorageImpl implements CsvBackupStorage {

    private static final String CSV_FILENAME_FORMAT = "backup_%d.csv";
    private static final String LOG_FILENAME_FORMAT = "error_%d.log";
    private final Path root;


    public CsvBackupStorageImpl(@Value("${csv.backup.root:./data/backups}") String rootDir) {
        this.root = Paths.get(rootDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new FileOperationException("CSV 백업 루트 디렉토리 생성 실패");
        }
    }

    @Override
    public String saveCsvFromStream(Long backupId, Stream<Employee> employeeStream)
        throws IOException {

        String filename = String.format(CSV_FILENAME_FORMAT, backupId);
        Path filePath = this.root.resolve(filename).normalize();

        Files.createDirectories(filePath.getParent());

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        String[] headers = {"ID", "EMP_number", "Name", "Email", "dept_id", "HireDate",
            "profile_images_id", "status",};

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(String.join(",", headers));
            writer.newLine();

            employeeStream.forEachOrdered(employee -> {
                try {
                    String[] row = employeeToStringArray(employee);
                    writer.write(
                        Arrays.stream(row).map(this::escapeCsv).collect(Collectors.joining(",")));
                    writer.newLine();
                } catch (IOException e) {
                    throw new FileOperationException("CSV 행 쓰기 중 오류 발생");
                }
            });

        } catch (IOException | FileOperationException e) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException suppress) {
                e.addSuppressed(suppress);
            }
            throw new FileOperationException("CSV 저장 실패");
        }
        return filePath.toString();
    }


    @Override
    public String saveErrorLog(Long backupId, Exception error) throws IOException {
        String filename = String.format(LOG_FILENAME_FORMAT, backupId);
        Path filePath = this.root.resolve(filename).normalize();

        Files.createDirectories(filePath.getParent());

        String stackTrace = Arrays.stream(error.getStackTrace()).map(StackTraceElement::toString)
            .collect(Collectors.joining("\n\t"));
        String errorMessage = String.format(
            "Backup Failed for ID: %d\nError: %s\nStackTrace:\n\t%s", backupId,
            error.getMessage() != null ? error.getMessage() : "Unknown error", stackTrace);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(errorMessage);
            writer.newLine();
        } catch (IOException e) {
            throw new FileOperationException("에러 로그 파일 저장 실패");
        }

        return filePath.toString();
    }


    @Override
    public InputStream load(String filename) {
        Path file = root.resolve(filename).normalize();

        if (Files.notExists(file)) {
            throw new FileOperationException("백업 파일을 찾을 수 없음.");
        }
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new FileOperationException("백업 파일 로드 실패");
        }
    }


    private String[] employeeToStringArray(Employee employee) {
        return new String[]{String.valueOf(employee.getId()),
            employee.getEmployeeNumber() != null ? employee.getEmployeeNumber() : "",
            employee.getName() != null ? employee.getName() : "",
            employee.getEmail() != null ? employee.getEmail() : "",
            employee.getDepartment() != null ? String.valueOf(employee.getDepartment().getId())
                : "", employee.getHireDate() != null ? employee.getHireDate().toString() : "",
            employee.getProfile() != null ? String.valueOf(employee.getProfile().getId()) : "",
            employee.getStatus() != null ? employee.getStatus().name() : ""};
    }

    private String escapeCsv(String field) {
        if (field == null) {
            return "";
        }
        boolean needsEscape =
            field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains(
                "\r");
        String escaped = field.replace("\"", "\"\"");
        return needsEscape ? "\"" + escaped + "\"" : escaped;
    }

    @Override
    public ResponseEntity<Resource> downloadResponse(Long id) {
        try {

            InputStream inputStream = get(id);

            Resource resource = new InputStreamResource(inputStream);

            String filename = String.format(CSV_FILENAME_FORMAT, id);

            String contentType = "text/csv";

            return ResponseEntity.ok()

                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"").body(resource);
        } catch (FileOperationException e) {

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public InputStream get(Long id) {
        Path file = resolvePath(id);
        if (Files.notExists(file)) {
            throw new FileOperationException("파일을 찾을 수 없음.");
        }
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new FileOperationException("파일을 읽을 수 없음.");
        }
    }

    private Path resolvePath(Long id) {
        String filename = String.format(CSV_FILENAME_FORMAT, id);
        return root.resolve(filename).normalize();
    }

    @Override
    public void deleteCsvFile(Long backupId) throws IOException {
        Path filePath = resolvePath(backupId);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileOperationException("백업 CSV 파일 삭제 실패");
        }
    }
}
