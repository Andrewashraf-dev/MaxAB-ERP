package com.example.erpsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee")  // lowercase table name is recommended
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "company_name_english")
    private String companyNameInEnglish;
    
    @Column(name = "company_name_arabic")
    private String companyNameInArabic;
    
    @Column(name = "employee_name_english")
    private String employeeNameInEnglish;
    
    @Column(name = "employee_name_arabic")
    private String employeeNameInArabic;
    
    @Column(name = "national_id")
    private String nationalId;
    
    @Column(name = "insurance_number")
    private String insuranceNumber;
    
    @Column(name = "title_english")
    private String titleInEnglish;
    
    @Column(name = "title_arabic")
    private String titleInArabic;
    
    @Column(name = "education_english")
    private String educationInEnglish;
    
    @Column(name = "education_arabic")
    private String educationInArabic;
    
    @Column(name = "address_english")
    private String addressInEnglish;
    
    @Column(name = "address_arabic")
    private String addressInArabic;
    
    @Column(name = "basic_salary")
    private BigDecimal basicSalary;
    
    @Column(name = "basic_salary_english_text")
    private String basicSalaryInEnglishText;
    
    @Column(name = "basic_salary_arabic_text")
    private String basicSalaryInArabicText;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    // Constructors
    public Employee() {
        this.createdAt = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getCompanyNameInEnglish() { return companyNameInEnglish; }
    public void setCompanyNameInEnglish(String companyNameInEnglish) { this.companyNameInEnglish = companyNameInEnglish; }
    
    public String getCompanyNameInArabic() { return companyNameInArabic; }
    public void setCompanyNameInArabic(String companyNameInArabic) { this.companyNameInArabic = companyNameInArabic; }
    
    public String getEmployeeNameInEnglish() { return employeeNameInEnglish; }
    public void setEmployeeNameInEnglish(String employeeNameInEnglish) { this.employeeNameInEnglish = employeeNameInEnglish; }
    
    public String getEmployeeNameInArabic() { return employeeNameInArabic; }
    public void setEmployeeNameInArabic(String employeeNameInArabic) { this.employeeNameInArabic = employeeNameInArabic; }
    
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    
    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }
    
    public String getTitleInEnglish() { return titleInEnglish; }
    public void setTitleInEnglish(String titleInEnglish) { this.titleInEnglish = titleInEnglish; }
    
    public String getTitleInArabic() { return titleInArabic; }
    public void setTitleInArabic(String titleInArabic) { this.titleInArabic = titleInArabic; }
    
    public String getEducationInEnglish() { return educationInEnglish; }
    public void setEducationInEnglish(String educationInEnglish) { this.educationInEnglish = educationInEnglish; }
    
    public String getEducationInArabic() { return educationInArabic; }
    public void setEducationInArabic(String educationInArabic) { this.educationInArabic = educationInArabic; }
    
    public String getAddressInEnglish() { return addressInEnglish; }
    public void setAddressInEnglish(String addressInEnglish) { this.addressInEnglish = addressInEnglish; }
    
    public String getAddressInArabic() { return addressInArabic; }
    public void setAddressInArabic(String addressInArabic) { this.addressInArabic = addressInArabic; }
    
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    
    public String getBasicSalaryInEnglishText() { return basicSalaryInEnglishText; }
    public void setBasicSalaryInEnglishText(String basicSalaryInEnglishText) { this.basicSalaryInEnglishText = basicSalaryInEnglishText; }
    
    public String getBasicSalaryInArabicText() { return basicSalaryInArabicText; }
    public void setBasicSalaryInArabicText(String basicSalaryInArabicText) { this.basicSalaryInArabicText = basicSalaryInArabicText; }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
