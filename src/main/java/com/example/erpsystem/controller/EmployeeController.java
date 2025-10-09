package com.example.erpsystem.controller;

import com.example.erpsystem.dto.EmployeeData;
import com.example.erpsystem.model.Employee;
import com.example.erpsystem.repository.EmployeeRepository;
import com.example.erpsystem.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    // 1. Landing Page
    @GetMapping("/")
    public String showIndex() {
        logger.info("Loading index page");
        return "index";
    }

    // 2. Show contract form
    @GetMapping("/exact-contract-form")
    public String showExactContractForm(Model model) {
        logger.info("Loading contract form");
        model.addAttribute("employeeData", new EmployeeData());
        return "exact-contract-form";
    }

    // 3. Generate contract (no DB save yet)
    @PostMapping("/generate-exact-contract")
    public String generateExactContract(@ModelAttribute EmployeeData data, Model model) {
        logger.info("Generating contract for: {}", data.getEmployeeNameInEnglish());
        logger.info("Received data - Start Date: {}, End Date: {}", data.getStartDate(), data.getEndDate());
        model.addAttribute("employeeData", data);
        return "exact-contract";
    }

    // 4. Save and show employee details
    @PostMapping("/save-and-view-employee")
    public String saveAndViewEmployee(@ModelAttribute EmployeeData dto, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Saving employee: {}", dto.getEmployeeNameInEnglish());
            logger.info("DTO data - Start Date: {}, End Date: {}", dto.getStartDate(), dto.getEndDate());
            
            Employee employee = EmployeeMapper.toEntity(dto);
            Employee savedEmployee = employeeRepository.save(employee);
            
            logger.info("Employee saved with ID: {}", savedEmployee.getId());
            
            EmployeeData savedDto = EmployeeMapper.toDto(savedEmployee);
            model.addAttribute("employee", savedDto);
            redirectAttributes.addFlashAttribute("success", "Employee saved successfully with ID: " + savedEmployee.getId());
            return "employee-details";
        } catch (Exception e) {
            logger.error("Error saving employee: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error saving employee: " + e.getMessage());
            return "redirect:/exact-contract-form";
        }
    }

    // 5. List all employees
    @GetMapping("/view-employees")
    public String viewEmployees(Model model) {
        try {
            List<Employee> employees = employeeRepository.findAll();
            logger.info("Found {} employees", employees.size());
            model.addAttribute("employees", employees);
        } catch (Exception e) {
            logger.error("Error loading employees: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading employees: " + e.getMessage());
        }
        return "view-employees";
    }

  // 6. View a specific employee - FIXED
@GetMapping("/employees/view/{id}")
public String viewEmployee(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
        logger.info("Viewing employee with ID: {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            // Convert Entity to DTO for the view
            EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
            logger.info("Employee found: {}", employeeData.getEmployeeNameInEnglish());
            model.addAttribute("employee", employeeData); // Use DTO instead of Entity
            return "employee-details";
        } else {
            logger.warn("Employee not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
            return "redirect:/view-employees";
        }
    } catch (Exception e) {
        logger.error("Error loading employee: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error", "Error loading employee: " + e.getMessage());
        return "redirect:/view-employees";
    }
}

    // 7. Edit a specific employee
    @GetMapping("/employees/edit/{id}")
    public String editEmployee(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Editing employee with ID: {}", id);
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
                logger.info("Loading employee data for edit: {}", employeeData.getEmployeeNameInEnglish());
                model.addAttribute("employeeData", employeeData);
                return "edit-employee";
            } else {
                logger.warn("Employee not found for edit with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
                return "redirect:/view-employees";
            }
        } catch (Exception e) {
            logger.error("Error loading employee for edit: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error loading employee for edit: " + e.getMessage());
            return "redirect:/view-employees";
        }
    }

    // 8. Update employee after edit
    @PostMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute EmployeeData updatedData, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Updating employee with ID: {}", id);
            Optional<Employee> optionalEmployee = employeeRepository.findById(id);
            if (optionalEmployee.isPresent()) {
                Employee employee = EmployeeMapper.toEntity(updatedData);
                employee.setId(id); // keep the ID
                employeeRepository.save(employee);
                logger.info("Employee updated successfully: {}", employee.getEmployeeNameInEnglish());
                redirectAttributes.addFlashAttribute("success", "Employee updated successfully");
            } else {
                logger.warn("Employee not found for update with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating employee: " + e.getMessage());
        }
        return "redirect:/view-employees";
    }

    // 9. Delete an employee
    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Deleting employee with ID: {}", id);
            if (employeeRepository.existsById(id)) {
                employeeRepository.deleteById(id);
                logger.info("Employee deleted successfully with ID: {}", id);
                redirectAttributes.addFlashAttribute("success", "Employee deleted successfully");
            } else {
                logger.warn("Employee not found for deletion with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error deleting employee: " + e.getMessage());
        }
        return "redirect:/view-employees";
    }

    // 10. Generate contract for specific employee - FIXED
@GetMapping("/employees/generate-contract/{id}")
public String generateEmployeeContract(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
        logger.info("Generating contract for employee ID: {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
            logger.info("Generating contract for: {}", employeeData.getEmployeeNameInEnglish());
            model.addAttribute("employeeData", employeeData);
            return "exact-contract";
        } else {
            logger.warn("Employee not found for contract generation with ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
            return "redirect:/view-employees";
        }
    } catch (Exception e) {
        logger.error("Error generating contract: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error", "Error generating contract: " + e.getMessage());
        return "redirect:/view-employees";
    }
}

   // 11. Generate insurance paper for specific employee - FIXED
@GetMapping("/employees/generate-insurance/{id}")
public String generateInsurancePaper(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
        logger.info("Generating insurance for employee ID: {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
            logger.info("Generating insurance for: {}", employeeData.getEmployeeNameInEnglish());
            model.addAttribute("employeeData", employeeData);
            return "insurance-paper";
        } else {
            logger.warn("Employee not found for insurance generation with ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
            return "redirect:/view-employees";
        }
    } catch (Exception e) {
        logger.error("Error generating insurance paper: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error", "Error generating insurance paper: " + e.getMessage());
        return "redirect:/view-employees";
    }
}

}
    