-- 1. ENUM 타입 정의 (테이블 생성 전에 실행)
CREATE TYPE employee_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'RESIGNED');
create type backup_status_enum as enum ('COMPLETED', 'FAILED', 'IN_PROGRESS', 'SKIPPED');
CREATE TYPE change_log_type_enum AS ENUM ('CREATE_EMPLOYEE', 'UPDATE_EMPLOYEE', 'DELETE_EMPLOYEE'); -- 예시 값 추가

-- 2. departments 테이블 생성
CREATE TABLE departments (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(30) NOT NULL UNIQUE,
                             description TEXT,
                             foundation_date DATE,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Java: Instant
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- Java: Instant, ON UPDATE는 트리거 필요
);


COMMENT ON TABLE departments IS '부서 정보';
COMMENT ON COLUMN departments.id IS '부서 아이디 (Auto-increment)';
COMMENT ON COLUMN departments.name IS '부서 이름';
COMMENT ON COLUMN departments.description IS '부서 설명';
COMMENT ON COLUMN departments.foundation_date IS '부서 설립일 (Java: LocalDate)';
COMMENT ON COLUMN departments.created_at IS '생성일시 (from Base, Java: Instant)';
COMMENT ON COLUMN departments.updated_at IS '수정일시 (from Base, Java: Instant)';

-- 3. binary_contents 테이블 생성
CREATE TABLE binary_contents (
                                 id BIGSERIAL PRIMARY KEY,
                                 file_name VARCHAR(100) NOT NULL,
                                 size BIGINT NOT NULL,
                                 content_type VARCHAR(30) NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Java: Instant

);
COMMIT ;
-- binary_contents 테이블 및 컬럼 주석 추가
COMMENT ON TABLE binary_contents IS '이진 파일 정보 (프로필 이미지, 백업 파일 등)';
COMMENT ON COLUMN binary_contents.id IS '파일 아이디 (Auto-increment)';
COMMENT ON COLUMN binary_contents.file_name IS '파일명 (Not Null)';
COMMENT ON COLUMN binary_contents.size IS '파일 크기 (Not Null)';
COMMENT ON COLUMN binary_contents.content_type IS '파일 헤더(타입, Not Null)';
COMMENT ON COLUMN binary_contents.created_at IS '생성일시 (from Base, Not Null, Java: Instant)';

-- 4. employees 테이블 생성
CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(30) NOT NULL,
                           email VARCHAR(100), -- Consider UNIQUE constraint if needed
                           emp_number VARCHAR(30) NOT NULL UNIQUE,
                           dept_id BIGINT NOT NULL,
                           position VARCHAR(30),
                           hire_date DATE, -- Java: LocalDate
                           status employee_status_enum NOT NULL, -- 정의된 ENUM 타입 사용
                           profile_image_id BIGINT NULL, -- Nullable
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Java: Instant
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Java: Instant, ON UPDATE는 트리거 필요
                           CONSTRAINT fk_employee_department FOREIGN KEY (dept_id) REFERENCES departments(id) ON DELETE RESTRICT,
                           CONSTRAINT fk_employee_profile_image FOREIGN KEY (profile_image_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- employees 테이블 및 컬럼 주석 추가
COMMENT ON TABLE employees IS '직원 정보';
COMMENT ON COLUMN employees.id IS '직원 아이디 (Auto-increment)';
COMMENT ON COLUMN employees.name IS '이름';
COMMENT ON COLUMN employees.email IS '이메일';
COMMENT ON COLUMN employees.emp_number IS '사원 번호';
COMMENT ON COLUMN employees.dept_id IS '부서 ID (FK to departments.id, ON DELETE RESTRICT)';
COMMENT ON COLUMN employees.position IS '직책';
COMMENT ON COLUMN employees.hire_date IS '입사일 (Java: LocalDate)';
COMMENT ON COLUMN employees.status IS '재직 상태 (Enum: ACTIVE, INACTIVE, RESIGNED)';
COMMENT ON COLUMN employees.profile_image_id IS '프로필 사진 ID (Nullable FK to binary_contents.id, ON DELETE SET NULL)';
COMMENT ON COLUMN employees.created_at IS '생성일시 (from Base, Java: Instant)';
COMMENT ON COLUMN employees.updated_at IS '수정일시 (from Base, Java: Instant)';

-- 5. change_logs 테이블 생성
CREATE TABLE change_logs (
                             id BIGSERIAL PRIMARY KEY,
                             type change_log_type_enum NOT NULL, -- 정의된 ENUM 타입 사용
                             emp_number VARCHAR(30) NOT NULL, -- FK target must be unique
                             before_value TEXT,
                             after_value TEXT,
                             memo TEXT,
                             ip_address VARCHAR(45),
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Java: Instant
                             CONSTRAINT fk_changelog_employee FOREIGN KEY (emp_number) REFERENCES employees(emp_number) ON DELETE CASCADE
);

-- change_logs 테이블 및 컬럼 주석 추가
COMMENT ON TABLE change_logs IS '변경 이력';
COMMENT ON COLUMN change_logs.id IS '수정 이력 아이디 (Auto-increment)';
COMMENT ON COLUMN change_logs.type IS '대상 작업 분류 (Enum: CREATE_EMPLOYEE, UPDATE_EMPLOYEE, DELETE_EMPLOYEE)';
COMMENT ON COLUMN change_logs.emp_number IS '대상 직원 사번 (FK to employees.emp_number, ON DELETE CASCADE)';
COMMENT ON COLUMN change_logs.before_value IS '변경 전 데이터';
COMMENT ON COLUMN change_logs.after_value IS '변경 후 데이터';
COMMENT ON COLUMN change_logs.memo IS '메모';
COMMENT ON COLUMN change_logs.ip_address IS '요청 IP 주소';
COMMENT ON COLUMN change_logs.created_at IS '생성일시 (from Base, Java: Instant)';

-- 6. backups 테이블 생성
CREATE TABLE backups (
                         id BIGSERIAL PRIMARY KEY,
                         worker VARCHAR(30),
                         status backup_status_enum NOT NULL, -- 정의된 ENUM 타입 사용
                         started_at TIMESTAMP NOT NULL, -- Java: Instant
                         ended_at TIMESTAMP NULL, -- Nullable, Java: Instant
                         file_id BIGINT NULL, -- Nullable
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Java: Instant
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Java: Instant, ON UPDATE는 트리거 필요
                         CONSTRAINT fk_backup_file FOREIGN KEY (file_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);


COMMENT ON TABLE backups IS '백업 정보';
COMMENT ON COLUMN backups.id IS '백업 아이디 (Auto-increment)';
COMMENT ON COLUMN backups.worker IS '작업자';
COMMENT ON COLUMN backups.status IS '진행 상태';
COMMENT ON COLUMN backups.started_at IS '백업 시작 시간 (Java: Instant)';
COMMENT ON COLUMN backups.ended_at IS '백업 종료 시간 (Nullable, Java: Instant)';
COMMENT ON COLUMN backups.created_at IS '생성일시 (from Base, Java: Instant)';
COMMENT ON COLUMN backups.updated_at IS '수정일시 (from Base, Java: Instant)';