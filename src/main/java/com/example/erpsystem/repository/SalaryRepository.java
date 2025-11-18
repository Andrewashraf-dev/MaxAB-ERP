// SalaryRepository.java
package com.example.erpsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.erpsystem.model.SalaryEntity;

import java.util.Date;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryEntity, Long> {
    
    // Find all salaries for a specific employee
    List<SalaryEntity> findByEmployeeId(Long employeeId);
    
    // Find salaries for an employee within a date range
    List<SalaryEntity> findByEmployeeIdAndSalaryDateBetween(Long employeeId, Date startDate, Date endDate);
    
    // Find all salaries within a date range
    List<SalaryEntity> findBySalaryDateBetween(Date startDate, Date endDate);
    
    // Find the latest salary for an employee
    @Query("SELECT s FROM SalaryEntity s WHERE s.employee.id = :employeeId ORDER BY s.salaryDate DESC")
    List<SalaryEntity> findLatestByEmployeeId(@Param("employeeId") Long employeeId);
    
    // Check if salary already exists for an employee in a specific month
    @Query("SELECT s FROM SalaryEntity s WHERE s.employee.id = :employeeId AND YEAR(s.salaryDate) = :year AND MONTH(s.salaryDate) = :month")
    List<SalaryEntity> findByEmployeeIdAndMonthAndYear(@Param("employeeId") Long employeeId, 
                                                      @Param("month") int month, 
                                                      @Param("year") int year);
    
    // Custom query to get salary statistics
    @Query("SELECT AVG(s.netSalary), MIN(s.netSalary), MAX(s.netSalary) FROM SalaryEntity s WHERE s.employee.id = :employeeId")
    Object[] findSalaryStatisticsByEmployeeId(@Param("employeeId") Long employeeId);
}