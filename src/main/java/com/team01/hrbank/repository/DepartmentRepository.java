package com.team01.hrbank.repository;

import com.team01.hrbank.entity.Department;
import com.team01.hrbank.repository.custom.DepartmentQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long>,
    DepartmentQueryRepository {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id); // 수정 시, 자기 자신(ID)을 제외한 중복 체크

}
