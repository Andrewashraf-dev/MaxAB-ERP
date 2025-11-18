package com.example.erpsystem.dto;

// SalaryRequestDTO.java
public class SalaryRequestDTO {
    private Long employeeId;
    private Double grossSalary;
    private Double contributionSalary;  // Changed from siSalary
    
    // Constructors
    public SalaryRequestDTO() {}
    
    public SalaryRequestDTO(Long employeeId, Double grossSalary, Double contributionSalary) {
        this.employeeId = employeeId;
        this.grossSalary = grossSalary;
        this.contributionSalary = contributionSalary;
    }
    
    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public Double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }
    
    public Double getContributionSalary() { return contributionSalary; }  // Changed
    public void setContributionSalary(Double contributionSalary) { this.contributionSalary = contributionSalary; }
}