package com.example.erpsystem.dto;

import java.util.Date;

// SalaryResponseDTO.java
public class SalaryResponseDTO {
    private Long salaryId;
    private Double grossSalary;
    private Double contributionSalary;
    private Double netSalary;
    private Date salaryDate;
    
    // DEFAULT CONSTRUCTOR (REQUIRED)
    public SalaryResponseDTO() {
    }
    
    // ADD THIS EXACT CONSTRUCTOR - it was missing
    public SalaryResponseDTO(Long salaryId, Double grossSalary, Double contributionSalary, Double netSalary, Date salaryDate) {
        this.salaryId = salaryId;
        this.grossSalary = grossSalary;
        this.contributionSalary = contributionSalary;
        this.netSalary = netSalary;
        this.salaryDate = salaryDate;
    }
    
    // Getters and setters
    public Long getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getContributionSalary() {
        return contributionSalary;
    }

    public void setContributionSalary(Double contributionSalary) {
        this.contributionSalary = contributionSalary;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }

    public Date getSalaryDate() {
        return salaryDate;
    }

    public void setSalaryDate(Date salaryDate) {
        this.salaryDate = salaryDate;
    }
}