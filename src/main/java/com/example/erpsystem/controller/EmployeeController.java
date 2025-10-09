package com.example.erpsystem.controller;

import com.example.erpsystem.dto.EmployeeData;
import com.example.erpsystem.model.Employee;
import com.example.erpsystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.erpsystem.mapper.EmployeeMapper;


import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // 1. Landing Page
    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    // 2. Show contract form
    @GetMapping("/exact-contract-form")
    public String showExactContractForm(Model model) {
        model.addAttribute("contractData", new EmployeeData());
        return "exact-contract-form";
    }

    // 3. Generate contract (no DB save yet)
    @PostMapping("/generate-exact-contract")
    public String generateExactContract(@ModelAttribute EmployeeData data, Model model) {
        model.addAttribute("contractData", data);
        return "exact-contract";
    }

    // 4. Save and show employee details
// When saving or updating:
@PostMapping("/save-and-view-employee")
public String saveAndViewEmployee(@ModelAttribute EmployeeData dto, Model model) {
    Employee employee = EmployeeMapper.toEntity(dto);
    // save employee to database using repository (not just in-memory list)
    employeeRepository.save(employee);
    
    EmployeeData savedDto = EmployeeMapper.toDto(employee);
    model.addAttribute("employee", savedDto);
    return "employee-details";
}

    // 5. List all employees
    @GetMapping("/view-employees")
    public String viewEmployees(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        return "view-employees";
    }

    // 6. View a specific employee
    @GetMapping("/employees/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employee-details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Employee not found");
            return "redirect:/view-employees";
        }
    }

    // 7. Edit a specific employee
  @GetMapping("/employees/edit/{id}")
public String editEmployee(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    Optional<Employee> employee = employeeRepository.findById(id);
    if (employee.isPresent()) {
        model.addAttribute("contractData", EmployeeMapper.toDto(employee.get()));
        return "edit-employee";
    } else {
        redirectAttributes.addFlashAttribute("error", "Employee not found");
        return "redirect:/view-employees";
    }
}


    // 8. Update employee after edit
  @PostMapping("/employees/update/{id}")
public String updateEmployee(@PathVariable Long id, @ModelAttribute EmployeeData updatedData, RedirectAttributes redirectAttributes) {
    Optional<Employee> optionalEmployee = employeeRepository.findById(id);
    if (optionalEmployee.isPresent()) {
        Employee employee = EmployeeMapper.toEntity(updatedData);
        employee.setId(id); // keep the ID
        employeeRepository.save(employee);
        redirectAttributes.addFlashAttribute("success", "Employee updated successfully");
    } else {
        redirectAttributes.addFlashAttribute("error", "Employee not found");
    }
    return "redirect:/view-employees";
}


    // 9. Delete an employee
    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Employee not found");
        }
        return "redirect:/view-employees";
    }

    // 10. Generate contract for specific employee
    @GetMapping("/employees/generate-contract/{id}")
    public String generateEmployeeContract(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("contractData", convertToDto(employee.get()));
            return "exact-contract";
        } else {
            redirectAttributes.addFlashAttribute("error", "Employee not found");
            return "redirect:/view-employees";
        }
    }

    // 11. Generate insurance paper for specific employee
    @GetMapping("/employees/generate-insurance/{id}")
    public String generateInsurancePaper(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("contractData", convertToDto(employee.get()));
            return "insurance-paper";
        } else {
            redirectAttributes.addFlashAttribute("error", "Employee not found");
            return "redirect:/view-employees";
        }
    }

    // Helper method: DTO to Entity
   private Employee convertToEntity(EmployeeData data) {
    return EmployeeMapper.toEntity(data);
}


    // Helper method: Entity to DTO
   
private EmployeeData convertToDto(Employee employee) {
    return EmployeeMapper.toDto(employee);
}
}
