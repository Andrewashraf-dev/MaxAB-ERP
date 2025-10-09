package com.example.erpsystem.mapper;

import com.example.erpsystem.dto.EmployeeData;
import com.example.erpsystem.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeData dto) {
        Employee employee = new Employee();

        if (dto.getId() != 0) {
            employee.setId((long) dto.getId());
        }

        // Convert String to LocalDate
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            employee.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            employee.setEndDate(LocalDate.parse(dto.getEndDate()));
        }

        employee.setCompanyNameInEnglish(dto.getCompanyNameInEnglish());
        employee.setCompanyNameInArabic(dto.getCompanyNameInArabic());
        employee.setEmployeeNameInEnglish(dto.getEmployeeNameInEnglish());
        employee.setEmployeeNameInArabic(dto.getEmployeeNameInArabic());
        employee.setNationalId(dto.getNationalId());
        employee.setInsuranceNumber(dto.getInsuranceNumber());
        employee.setTitleInEnglish(dto.getTitleInEnglish());
        employee.setTitleInArabic(dto.getTitleInArabic());
        employee.setEducationInEnglish(dto.getEducationInEnglish());
        employee.setEducationInArabic(dto.getEducationInArabic());
        employee.setAddressInEnglish(dto.getAddressInEnglish());
        employee.setAddressInArabic(dto.getAddressInArabic());

        // Convert salary from String to BigDecimal
        if (dto.getBasicSalaryInEnglish() != null && !dto.getBasicSalaryInEnglish().isEmpty()) {
            try {
                employee.setBasicSalary(new BigDecimal(dto.getBasicSalaryInEnglish()));
            } catch (NumberFormatException e) {
                employee.setBasicSalary(BigDecimal.ZERO);
            }
        }

        employee.setBasicSalaryInEnglishText(dto.getBasicSalaryInEnglishText());
        employee.setBasicSalaryInArabicText(dto.getBasicSalaryInArabicText());

        return employee;
    }

    public static EmployeeData toDto(Employee employee) {
        EmployeeData dto = new EmployeeData();

        if (employee.getId() != null) {
            dto.setId(employee.getId().intValue());
        }

        if (employee.getStartDate() != null) {
            dto.setStartDate(employee.getStartDate().toString());
        }
        if (employee.getEndDate() != null) {
            dto.setEndDate(employee.getEndDate().toString());
        }

        dto.setCompanyNameInEnglish(employee.getCompanyNameInEnglish());
        dto.setCompanyNameInArabic(employee.getCompanyNameInArabic());
        dto.setEmployeeNameInEnglish(employee.getEmployeeNameInEnglish());
        dto.setEmployeeNameInArabic(employee.getEmployeeNameInArabic());
        dto.setNationalId(employee.getNationalId());
        dto.setInsuranceNumber(employee.getInsuranceNumber());
        dto.setTitleInEnglish(employee.getTitleInEnglish());
        dto.setTitleInArabic(employee.getTitleInArabic());
        dto.setEducationInEnglish(employee.getEducationInEnglish());
        dto.setEducationInArabic(employee.getEducationInArabic());
        dto.setAddressInEnglish(employee.getAddressInEnglish());
        dto.setAddressInArabic(employee.getAddressInArabic());

        // Convert BigDecimal to String
        if (employee.getBasicSalary() != null) {
            dto.setBasicSalaryInEnglish(employee.getBasicSalary().toString());
        }

        dto.setBasicSalaryInEnglishText(employee.getBasicSalaryInEnglishText());
        dto.setBasicSalaryInArabicText(employee.getBasicSalaryInArabicText());

        return dto;
    }
}
