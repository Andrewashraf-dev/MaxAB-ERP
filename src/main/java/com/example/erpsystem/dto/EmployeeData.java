package com.example.erpsystem.dto;

import java.math.BigDecimal;

public class EmployeeData {

    private int id;
    private String startDate;
    private String endDate;
    private String companyNameInEnglish;
    private String companyNameInArabic;
    private String employeeNameInEnglish;
    private String employeeNameInArabic;
    private String nationalId;
    private String insuranceNumber;
    private String titleInEnglish;
    private String titleInArabic;
    private String educationInEnglish;
    private String educationInArabic;
    private String addressInEnglish;
    private String addressInArabic;
    private String basicSalaryInEnglish;
    private String basicSalaryInArabic;
    private String basicSalaryInEnglishText;
    private String basicSalaryInArabicText;
    private String companyInsuranceNumber;
    private String companyTaxNumber;
    private String jobTitleCode;
    private String contributionSalary;
    private String employeePhoto;
    private String variableSalaryInNumber;
    private String variableSalaryInEnglishText;
    private String variableSalaryInArabicText;

    // Add numeric fields for actual storage
    private BigDecimal basicSalary;
    private BigDecimal contributionSalaryNumeric;
    

    // Getters and setters for all fields...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
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
    
    public String getBasicSalaryInEnglish() { return basicSalaryInEnglish; }
    public void setBasicSalaryInEnglish(String basicSalaryInEnglish) { 
        this.basicSalaryInEnglish = basicSalaryInEnglish;
        // Convert to numeric when set
        if (basicSalaryInEnglish != null && !basicSalaryInEnglish.trim().isEmpty()) {
            try {
                String cleanValue = basicSalaryInEnglish.replaceAll("[^\\d.]", "");
                this.basicSalary = new BigDecimal(cleanValue);
            } catch (NumberFormatException e) {
                this.basicSalary = BigDecimal.ZERO;
            }
        }
    }
    
    public String getBasicSalaryInArabic() { return basicSalaryInArabic; }
    public void setBasicSalaryInArabic(String basicSalaryInArabic) { 
        this.basicSalaryInArabic = basicSalaryInArabic;
        // Also set the numeric value from Arabic field if English is empty
        if ((this.basicSalary == null || this.basicSalary.equals(BigDecimal.ZERO)) && 
            basicSalaryInArabic != null && !basicSalaryInArabic.trim().isEmpty()) {
            try {
                String cleanValue = basicSalaryInArabic.replaceAll("[^\\d.]", "");
                this.basicSalary = new BigDecimal(cleanValue);
            } catch (NumberFormatException e) {
                this.basicSalary = BigDecimal.ZERO;
            }
        }
    }
    
    public String getBasicSalaryInEnglishText() { return basicSalaryInEnglishText; }
    public void setBasicSalaryInEnglishText(String basicSalaryInEnglishText) { this.basicSalaryInEnglishText = basicSalaryInEnglishText; }
    
    public String getBasicSalaryInArabicText() { return basicSalaryInArabicText; }
    public void setBasicSalaryInArabicText(String basicSalaryInArabicText) { this.basicSalaryInArabicText = basicSalaryInArabicText; }

    public String getCompanyInsuranceNumber() { return companyInsuranceNumber; }
    public void setCompanyInsuranceNumber(String companyInsuranceNumber) { this.companyInsuranceNumber = companyInsuranceNumber; }
    
    public String getCompanyTaxNumber() { return companyTaxNumber; }
    public void setCompanyTaxNumber(String companyTaxNumber) { this.companyTaxNumber = companyTaxNumber; }

    public String getJobTitleCode() { return jobTitleCode; }
    public void setJobTitleCode(String jobTitleCode) { this.jobTitleCode = jobTitleCode; }

    
    public String getContributionSalary() { return contributionSalary; }
    public void setContributionSalary(String contributionSalary) { 
        this.contributionSalary = contributionSalary;
        // Convert to numeric when set
        if (contributionSalary != null && !contributionSalary.trim().isEmpty()) {
            try {
                String cleanValue = contributionSalary.replaceAll("[^\\d.]", "");
                this.contributionSalaryNumeric = new BigDecimal(cleanValue);
            } catch (NumberFormatException e) {
                this.contributionSalaryNumeric = BigDecimal.ZERO;
            }
        }
    }

    // Numeric getters and setters
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { 
        this.basicSalary = basicSalary;
        // Also update string representations
        if (basicSalary != null) {
            this.basicSalaryInEnglish = basicSalary.toString();
            this.basicSalaryInArabic = basicSalary.toString();
        }
    }
    
    public BigDecimal getContributionSalaryNumeric() { return contributionSalaryNumeric; }
    public void setContributionSalaryNumeric(BigDecimal contributionSalaryNumeric) { 
        this.contributionSalaryNumeric = contributionSalaryNumeric;
        if (contributionSalaryNumeric != null) {
            this.contributionSalary = contributionSalaryNumeric.toString();
        }
    }

    public String getEmployeePhoto() { return employeePhoto; }
    public void setEmployeePhoto(String employeePhoto) { this.employeePhoto = employeePhoto; }

    public String getVariableSalaryInNumber() { return variableSalaryInNumber; }
    public void setVariableSalaryInNumber(String variableSalaryInNumber) { 
        this.variableSalaryInNumber = variableSalaryInNumber;
    }

    public String getVariableSalaryInEnglishText() { return variableSalaryInEnglishText; }
    public void setVariableSalaryInEnglishText(String variableSalaryInEnglishText) { 
        this.variableSalaryInEnglishText = variableSalaryInEnglishText;
    }

    public String getVariableSalaryInArabicText() { return variableSalaryInArabicText; }
    public void setVariableSalaryInArabicText(String variableSalaryInArabicText) { 
        this.variableSalaryInArabicText = variableSalaryInArabicText;
    }    
}
