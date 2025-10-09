package com.example.erpsystem.service;

import com.example.erpsystem.model.Employee;
import com.example.erpsystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository EmployeeRepository;

    public List<Employee> getAllContracts() {
        return EmployeeRepository.findAll();
    }

    public Optional<Employee> getContractById(Long id) {
        return EmployeeRepository.findById(id);
    }

    public Employee saveContract(Employee contract) {
        return EmployeeRepository.save(contract);
    }

    public void deleteContract(Long id) {
        EmployeeRepository.deleteById(id);
    }

    public List<Employee> searchByEmployeeName(String name) {
        return EmployeeRepository.findByEmployeeNameInEnglishContainingIgnoreCase(name);
    }

    public List<Employee> searchByCompanyName(String companyName) {
        return EmployeeRepository.findByCompanyNameInEnglishContainingIgnoreCase(companyName);
    }
}