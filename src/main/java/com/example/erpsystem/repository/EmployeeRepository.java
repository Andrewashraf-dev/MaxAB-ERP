package com.example.erpsystem.repository;

import com.example.erpsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByEmployeeNameInEnglishContainingIgnoreCase(String name);
    List<Employee> findByCompanyNameInEnglishContainingIgnoreCase(String companyName);
}