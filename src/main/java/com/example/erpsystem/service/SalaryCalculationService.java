package com.example.erpsystem.service;

import org.springframework.stereotype.Service;
import com.example.erpsystem.model.SalaryEntity;

@Service
public class SalaryCalculationService {
    
    private static final Double ANNUAL_DEDUCTION = 20000.0;
    
    public Double calculateNetSalary(Double grossSalary, Double contributionSalary) {
    // 1. Company Share SOC Insurance = Contribution Salary * 0.1875
    Double companyShareSocInsurance = contributionSalary * 0.1875;
    
    // 2. Employee Share SOC Insurance = Contribution Salary * 0.11
    Double employeeShareSocInsurance = contributionSalary * 0.11;
    
    // 3. Martyrs Fund = Gross Salary * 0.0005
    Double martyrsFund = grossSalary * 0.0005;
    
    // ORIGINAL WORKING FORMULA: Annual Tax Pools = ((Gross Salary - Employee Share SOC Insurance) * 12 - 20000)
    Double annualTaxPools = ((grossSalary - employeeShareSocInsurance) * 12) - ANNUAL_DEDUCTION;
    
    // 5. Taxes (Basic) - Annual calculation divided by 12 for monthly
    Double annualTaxesBasic = calculateTaxesBasic(annualTaxPools);
    Double monthlyTaxesBasic = annualTaxesBasic / 12;
    
    // 6. Net Salary = Gross Salary - Employee Share SOC Insurance - Taxes Basic - Martyrs Fund
    Double netSalary = grossSalary - employeeShareSocInsurance - monthlyTaxesBasic - martyrsFund;
    
    return round(netSalary);
}

public SalaryEntity calculateAndPopulateSalary(SalaryEntity salary) {
    Double grossSalary = salary.getGrossSalary();
    Double contributionSalary = salary.getContributionSalary();

    // Perform all calculations
    Double companyShareSocInsurance = contributionSalary * 0.1875;
    Double employeeShareSocInsurance = contributionSalary * 0.11;
    Double martyrsFund = grossSalary * 0.0005;
    
    // ORIGINAL WORKING FORMULA: Annual Tax Pools = ((Gross Salary - Employee Share SOC Insurance) * 12 - 20000)
    Double annualTaxPools = ((grossSalary - employeeShareSocInsurance) * 12) - ANNUAL_DEDUCTION;
    
    Double annualTaxesBasic = calculateTaxesBasic(annualTaxPools);
    Double monthlyTaxesBasic = annualTaxesBasic / 12;
    Double netSalary = grossSalary - employeeShareSocInsurance - monthlyTaxesBasic - martyrsFund;
    
    // Set all fields
    salary.setCompanyShareSocInsurance(round(companyShareSocInsurance));
    salary.setEmployeeShareSocInsurance(round(employeeShareSocInsurance));
    salary.setMartyrsFund(round(martyrsFund));
    salary.setAnnualTaxPools(round(annualTaxPools));
    salary.setTaxesBasic(round(monthlyTaxesBasic));
    salary.setNetSalary(round(netSalary));
    
    return salary;
}
    
    // calculateTaxesBasic method remains the same...
    private Double calculateTaxesBasic(Double annualTaxPools) {
        if (annualTaxPools <= 0) {
            return 0.0;
        } else if (annualTaxPools <= 40000) {
            return 0.0;
        } else if (annualTaxPools <= 55000) {
            return (annualTaxPools - 40000) * 0.10;
        } else if (annualTaxPools <= 70000) {
            return ((annualTaxPools - 55000) * 0.15) + 1500;
        } else if (annualTaxPools <= 200000) {
            return ((annualTaxPools - 70000) * 0.20) + 1500 + 2250;
        } else if (annualTaxPools <= 400000) {
            return ((annualTaxPools - 200000) * 0.225) + 1500 + 2250 + 26000;
        } else if (annualTaxPools <= 600000) {
            return ((annualTaxPools - 400000) * 0.25) + 1500 + 2250 + 26000 + 45000;
        } else if (annualTaxPools <= 700000) {
            return ((annualTaxPools - 400000) * 0.25) + 5500 + 2250 + 26000 + 45000;
        } else if (annualTaxPools <= 800000) {
            return ((annualTaxPools - 400000) * 0.25) + 10500 + 26000 + 45000;
        } else if (annualTaxPools <= 900000) {
            return ((annualTaxPools - 400000) * 0.25) + 40000 + 45000;
        } else if (annualTaxPools <= 1200000) {
            return ((annualTaxPools - 400000) * 0.25) + 90000;
        } else {
            return ((annualTaxPools - 1200000) * 0.275) + 300000;
        }
    }
    
    private Double round(Double value) {
        if (value == null) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }
}