package com.example.erpsystem.mapper;

import com.example.erpsystem.dto.EmployeeData;
import com.example.erpsystem.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmployeeMapper {
    
    public static Employee toEntity(EmployeeData dto) {
        Employee entity = new Employee();
        
        // Convert dates from String to LocalDate
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            try {
                entity.setStartDate(LocalDate.parse(dto.getStartDate()));
            } catch (Exception e) {
                System.err.println("Error parsing start date: " + dto.getStartDate());
            }
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            try {
                entity.setEndDate(LocalDate.parse(dto.getEndDate()));
            } catch (Exception e) {
                System.err.println("Error parsing end date: " + dto.getEndDate());
            }
        }
        
        // Set other fields
        entity.setCompanyNameInEnglish(dto.getCompanyNameInEnglish());
        entity.setCompanyNameInArabic(dto.getCompanyNameInArabic());
        entity.setEmployeeNameInEnglish(dto.getEmployeeNameInEnglish());
        entity.setEmployeeNameInArabic(dto.getEmployeeNameInArabic());
        entity.setNationalId(dto.getNationalId());
        entity.setInsuranceNumber(dto.getInsuranceNumber());
        entity.setTitleInEnglish(dto.getTitleInEnglish());
        entity.setTitleInArabic(dto.getTitleInArabic());
        entity.setEducationInEnglish(dto.getEducationInEnglish());
        entity.setEducationInArabic(dto.getEducationInArabic());
        entity.setAddressInEnglish(dto.getAddressInEnglish());
        entity.setAddressInArabic(dto.getAddressInArabic());
        entity.setCompanyInsuranceNumber(dto.getCompanyInsuranceNumber());
        entity.setCompanyTaxNumber(dto.getCompanyTaxNumber());
        entity.setJobTitleCode(dto.getJobTitleCode());
        entity.setEmployeePhoto(dto.getEmployeePhoto()); 
        
        // Convert salary - handle both English and Arabic salary fields with comma removal
        BigDecimal basicSalary = parseSalaryFromString(dto.getBasicSalaryInEnglish());
        if (basicSalary == null) {
            basicSalary = parseSalaryFromString(dto.getBasicSalaryInArabic());
        }
        entity.setBasicSalary(basicSalary != null ? basicSalary : BigDecimal.ZERO);
        
        // Convert contribution salary with comma removal
        BigDecimal contributionSalary = parseSalaryFromString(dto.getContributionSalary());
        entity.setContributionSalary(contributionSalary != null ? contributionSalary : BigDecimal.ZERO);

        // Convert variable salary
        if (dto.getVariableSalaryInNumber() != null && !dto.getVariableSalaryInNumber().isEmpty()) {
            try {
                String cleanValue = dto.getVariableSalaryInNumber().replaceAll("[^\\d.]", "");
                entity.setVariableSalaryNumber(new BigDecimal(cleanValue));
            } catch (NumberFormatException e) {
                entity.setVariableSalaryNumber(BigDecimal.ZERO);
            }
        }

          entity.setVariableSalaryInEnglishText(dto.getVariableSalaryInEnglishText());
        entity.setVariableSalaryInArabicText(dto.getVariableSalaryInArabicText());
        
        entity.setBasicSalaryInEnglishText(dto.getBasicSalaryInEnglishText());
        entity.setBasicSalaryInArabicText(dto.getBasicSalaryInArabicText());
        
        return entity;
    }
    
    public static EmployeeData toDto(Employee entity) {
        EmployeeData dto = new EmployeeData();
        
        // Convert ID from Long to int
        if (entity.getId() != null) {
            dto.setId(entity.getId().intValue());
        }
        
        // Convert dates from LocalDate to String
        if (entity.getStartDate() != null) {
            dto.setStartDate(entity.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (entity.getEndDate() != null) {
            dto.setEndDate(entity.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        // Set other fields
        dto.setCompanyNameInEnglish(entity.getCompanyNameInEnglish());
        dto.setCompanyNameInArabic(entity.getCompanyNameInArabic());
        dto.setEmployeeNameInEnglish(entity.getEmployeeNameInEnglish());
        dto.setEmployeeNameInArabic(entity.getEmployeeNameInArabic());
        dto.setNationalId(entity.getNationalId());
        dto.setInsuranceNumber(entity.getInsuranceNumber());
        dto.setTitleInEnglish(entity.getTitleInEnglish());
        dto.setTitleInArabic(entity.getTitleInArabic());
        dto.setEducationInEnglish(entity.getEducationInEnglish());
        dto.setEducationInArabic(entity.getEducationInArabic());
        dto.setAddressInEnglish(entity.getAddressInEnglish());
        dto.setAddressInArabic(entity.getAddressInArabic());
        dto.setCompanyInsuranceNumber(entity.getCompanyInsuranceNumber());
        dto.setCompanyTaxNumber(entity.getCompanyTaxNumber());
        dto.setJobTitleCode(entity.getJobTitleCode());
        dto.setEmployeePhoto(entity.getEmployeePhoto()); 
        
        // Convert salary - format with commas for display
        if (entity.getBasicSalary() != null) {
            dto.setBasicSalaryInEnglish(formatNumberWithCommas(entity.getBasicSalary()));
            dto.setBasicSalaryInArabic(formatNumberWithCommas(entity.getBasicSalary()));
        } else {
            dto.setBasicSalaryInEnglish("0");
            dto.setBasicSalaryInArabic("0");
        }
        
        // Convert contribution salary - format with commas for display
        if (entity.getContributionSalary() != null) {
            dto.setContributionSalary(formatNumberWithCommas(entity.getContributionSalary()));
        } else {
            dto.setContributionSalary("0");
        }

         // Convert variable salary
        if (entity.getVariableSalaryNumber() != null) {
            dto.setVariableSalaryInNumber(formatNumberWithCommas(entity.getVariableSalaryNumber()));
        } else {
            dto.setVariableSalaryInNumber("0");
        }

        
        dto.setVariableSalaryInEnglishText(entity.getVariableSalaryInEnglishText());
        dto.setVariableSalaryInArabicText(entity.getVariableSalaryInArabicText());
        
        dto.setBasicSalaryInEnglishText(entity.getBasicSalaryInEnglishText());
        dto.setBasicSalaryInArabicText(entity.getBasicSalaryInArabicText());
        
        return dto;
    }
    
    private static BigDecimal parseSalaryFromString(String salaryString) {
        if (salaryString == null || salaryString.trim().isEmpty()) {
            return null;
        }
        try {
            // Remove commas and any non-digit characters except decimal point
            String cleanValue = salaryString.replaceAll("[^\\d.]", "");
            if (cleanValue.isEmpty()) {
                return null;
            }
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing salary string: " + salaryString);
            return null;
        }
    }
    
    private static String formatNumberWithCommas(BigDecimal number) {
        if (number == null) {
            return "0";
        }
        try {
            // Format with commas for thousands
            return String.format("%,d", number.longValue());
        } catch (Exception e) {
            return number.toString();
        }
    }
    
}

