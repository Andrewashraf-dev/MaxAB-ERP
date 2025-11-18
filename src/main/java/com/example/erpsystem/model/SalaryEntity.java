package com.example.erpsystem.model;

import jakarta.persistence.*;
import java.util.Date;

// SalaryEntity.java
@Entity
@Table(name = "salary")
public class SalaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    // User inputs
    private Double grossSalary;
    private Double contributionSalary;  // Changed from siSalary
    
    // Calculated result
    private Double netSalary;
    
    // Backend calculations
    private Double companyShareSocInsurance;
    private Double employeeShareSocInsurance;
    private Double martyrsFund;
    private Double annualTaxPools;
    private Double taxesBasic;
    
    @Temporal(TemporalType.DATE)
    private Date salaryDate;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    
    public Double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }
    
    public Double getContributionSalary() { return contributionSalary; }  // Changed
    public void setContributionSalary(Double contributionSalary) { this.contributionSalary = contributionSalary; }
    
    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
    
    public Date getSalaryDate() { return salaryDate; }
    public void setSalaryDate(Date salaryDate) { this.salaryDate = salaryDate; }
    
    // Other getters and setters...
    public Double getCompanyShareSocInsurance() { return companyShareSocInsurance; }
    public void setCompanyShareSocInsurance(Double companyShareSocInsurance) { this.companyShareSocInsurance = companyShareSocInsurance; }
    
    public Double getEmployeeShareSocInsurance() { return employeeShareSocInsurance; }
    public void setEmployeeShareSocInsurance(Double employeeShareSocInsurance) { this.employeeShareSocInsurance = employeeShareSocInsurance; }
    
    public Double getMartyrsFund() { return martyrsFund; }
    public void setMartyrsFund(Double martyrsFund) { this.martyrsFund = martyrsFund; }
    
    public Double getAnnualTaxPools() { return annualTaxPools; }
    public void setAnnualTaxPools(Double annualTaxPools) { this.annualTaxPools = annualTaxPools; }
    
    public Double getTaxesBasic() { return taxesBasic; }
    public void setTaxesBasic(Double taxesBasic) { this.taxesBasic = taxesBasic; }
}
