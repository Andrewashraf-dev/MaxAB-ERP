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
            entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            entity.setEndDate(LocalDate.parse(dto.getEndDate()));
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
        
        // Convert salary
        if (dto.getBasicSalaryInEnglish() != null && !dto.getBasicSalaryInEnglish().isEmpty()) {
            try {
                entity.setBasicSalary(new BigDecimal(dto.getBasicSalaryInEnglish()));
            } catch (NumberFormatException e) {
                entity.setBasicSalary(BigDecimal.ZERO);
            }
        }
        
        entity.setBasicSalaryInEnglishText(dto.getBasicSalaryInEnglishText());
        entity.setBasicSalaryInArabicText(dto.getBasicSalaryInArabicText());
        
        return entity;
    }
    
    public static EmployeeData toDto(Employee entity) {
        EmployeeData dto = new EmployeeData();
        
        // Convert ID from Long to int
        dto.setId(entity.getId().intValue());
        
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
        
        // Convert salary
        if (entity.getBasicSalary() != null) {
            dto.setBasicSalaryInEnglish(entity.getBasicSalary().toString());
        }
        
        dto.setBasicSalaryInEnglishText(entity.getBasicSalaryInEnglishText());
        dto.setBasicSalaryInArabicText(entity.getBasicSalaryInArabicText());
        
        return dto;
    }
}