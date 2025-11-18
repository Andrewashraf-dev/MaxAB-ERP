// SalaryController.java - updated imports
package com.example.erpsystem.controller;

import com.example.erpsystem.dto.SalaryRequestDTO;
import com.example.erpsystem.dto.SalaryResponseDTO;
import com.example.erpsystem.model.Employee;
import com.example.erpsystem.model.SalaryEntity;
import com.example.erpsystem.repository.SalaryRepository;
import com.example.erpsystem.repository.EmployeeRepository;
import com.example.erpsystem.service.SalaryCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryController {
    
    @Autowired
    private SalaryCalculationService salaryCalculationService;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @PostMapping("/calculate-net-salary")
    public ResponseEntity<Map<String, Object>> calculateNetSalary(@RequestBody SalaryRequestDTO request) {
        try {
            Double netSalary = salaryCalculationService.calculateNetSalary(
                request.getGrossSalary(), 
                request.getContributionSalary()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("netSalary", netSalary);
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Calculation failed: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSalary(@RequestBody SalaryRequestDTO request) {
        try {
            // Check if employee exists
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee with ID " + request.getEmployeeId() + " not found"));
            
            SalaryEntity salary = new SalaryEntity();
            salary.setEmployee(employee);
            salary.setGrossSalary(request.getGrossSalary());
            salary.setContributionSalary(request.getContributionSalary());
            salary.setSalaryDate(new Date());
            
            salaryCalculationService.calculateAndPopulateSalary(salary);
            
            SalaryEntity savedSalary = salaryRepository.save(salary);
            
            // Create response using setters (to avoid constructor issues)
            SalaryResponseDTO response = new SalaryResponseDTO();
            response.setSalaryId(savedSalary.getId());
            response.setGrossSalary(savedSalary.getGrossSalary());
            response.setContributionSalary(savedSalary.getContributionSalary());
            response.setNetSalary(savedSalary.getNetSalary());
            response.setSalaryDate(savedSalary.getSalaryDate());
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("message", "Salary saved successfully");
            successResponse.put("data", response);
            
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("error", "Failed to save salary: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeSalaries(@PathVariable Long employeeId) {
        try {
            List<SalaryEntity> salaries = salaryRepository.findByEmployeeId(employeeId);
            
            List<SalaryResponseDTO> response = salaries.stream()
                .map(salary -> {
                    SalaryResponseDTO dto = new SalaryResponseDTO();
                    dto.setSalaryId(salary.getId());
                    dto.setGrossSalary(salary.getGrossSalary());
                    dto.setContributionSalary(salary.getContributionSalary());
                    dto.setNetSalary(salary.getNetSalary());
                    dto.setSalaryDate(salary.getSalaryDate());
                    return dto;
                })
                .collect(Collectors.toList());
                
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", "success");
            successResponse.put("data", response);
            
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("error", "Failed to retrieve salaries: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}