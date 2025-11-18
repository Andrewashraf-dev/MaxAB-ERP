package com.example.erpsystem.controller;

import com.example.erpsystem.dto.EmployeeData;
import com.example.erpsystem.repository.EmployeeRepository;
import com.example.erpsystem.mapper.EmployeeMapper;
import com.example.erpsystem.model.Employee;
import com.example.erpsystem.service.PDFService;
import com.example.erpsystem.service.TranslationService;
import com.example.erpsystem.service.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Controller
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeRepository employeeRepository;
    private final PDFService pdfService;
    private final TranslationService translationService;
    private final FileStorageService fileStorageService;

    // Constructor injection instead of field injection
    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository,
                            PDFService pdfService,
                            TranslationService translationService,
                            FileStorageService fileStorageService) {
        this.employeeRepository = employeeRepository;
        this.pdfService = pdfService;
        this.translationService = translationService;
        this.fileStorageService = fileStorageService;
    }

    // 1. Landing Page
    @GetMapping("/")
    public String showIndex() {
        logger.info("Loading home page");
        return "home";
    }

    // 2. Show contract form
    @GetMapping("/employee-data-form")
    public String showExactContractForm(Model model) {
        logger.info("Loading contract form");
        model.addAttribute("employeeData", new EmployeeData());
        return "employee-data-form";
    }

    // 3. Generate contract (no DB save yet)
    @PostMapping("/generate-contract")
    public String generateExactContract(@ModelAttribute EmployeeData data, Model model) {
        logger.info("Generating contract for: {}", data.getEmployeeNameInEnglish());
        logger.info("Received data - Start Date: {}, End Date: {}", data.getStartDate(), data.getEndDate());
        
        model.addAttribute("employeeData", data);
        return "contract-template";
    }

    // 4. Save and show employee details - UPDATED WITH FILE UPLOAD
    @PostMapping("/save-and-view-employee")
    public String saveAndViewEmployee(
            @ModelAttribute EmployeeData dto,
            @RequestParam(value = "employeePhotoFile", required = false) MultipartFile employeePhotoFile,
            Model model, 
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Saving employee: {}", dto.getEmployeeNameInEnglish());
            logger.info("Received data - Start Date: {}, End Date: {}", dto.getStartDate(), dto.getEndDate());
            
            // Handle file upload
            if (employeePhotoFile != null && !employeePhotoFile.isEmpty()) {
                try {
                    String fileName = fileStorageService.storeFile(employeePhotoFile);
                    dto.setEmployeePhoto("/uploads/" + fileName);
                    logger.info("Employee photo saved: {}", fileName);
                } catch (Exception e) {
                    logger.error("Error saving employee photo: {}", e.getMessage(), e);
                    redirectAttributes.addFlashAttribute("error", "Error saving employee photo: " + e.getMessage());
                    return "redirect:/employee-data-form";
                }
            }
            
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
            return "redirect:/employee-data-form";
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

    // 6. View a specific employee
    @GetMapping("/employees/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Viewing employee with ID: {}", id);
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
                logger.info("Employee found: {}", employeeData.getEmployeeNameInEnglish());
                model.addAttribute("employee", employeeData);
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

    // 8. Update employee after edit - UPDATED WITH FILE UPLOAD
    @PostMapping("/employees/update/{id}")
    public String updateEmployee(
            @PathVariable Long id, 
            @ModelAttribute EmployeeData updatedData,
            @RequestParam(value = "employeePhotoFile", required = false) MultipartFile employeePhotoFile,
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Updating employee with ID: {}", id);
            Optional<Employee> optionalEmployee = employeeRepository.findById(id);
            if (optionalEmployee.isPresent()) {
                Employee existingEmployee = optionalEmployee.get();
                
                // Handle file upload
                if (employeePhotoFile != null && !employeePhotoFile.isEmpty()) {
                    try {
                        String fileName = fileStorageService.storeFile(employeePhotoFile);
                        updatedData.setEmployeePhoto("/uploads/" + fileName);
                        logger.info("Employee photo updated: {}", fileName);
                    } catch (Exception e) {
                        logger.error("Error updating employee photo: {}", e.getMessage(), e);
                        redirectAttributes.addFlashAttribute("error", "Error updating employee photo: " + e.getMessage());
                        return "redirect:/employees/edit/" + id;
                    }
                } else {
                    // Keep existing photo if no new file uploaded
                    updatedData.setEmployeePhoto(existingEmployee.getEmployeePhoto());
                }
                
                // Update the existing entity with new data
                Employee updatedEmployee = EmployeeMapper.toEntity(updatedData);
                updatedEmployee.setId(id);
                updatedEmployee.setCreatedAt(existingEmployee.getCreatedAt());
                // Remove setUpdatedAt() call since it doesn't exist in Employee entity
                
                Employee savedEmployee = employeeRepository.save(updatedEmployee);
                logger.info("Employee updated successfully: {}", savedEmployee.getEmployeeNameInEnglish());
                
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
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                // Delete associated photo file if exists
                String photoPath = employee.get().getEmployeePhoto();
                if (photoPath != null && !photoPath.isEmpty()) {
                    try {
                        fileStorageService.deleteFile(photoPath);
                        logger.info("Deleted employee photo: {}", photoPath);
                    } catch (Exception e) {
                        logger.warn("Could not delete employee photo: {}", e.getMessage());
                    }
                }
                
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

    // 10. Generate contract for specific employee
    @GetMapping("/employees/generate-contract/{id}")
    public String generateEmployeeContract(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Generating contract for employee ID: {}", id);
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
                logger.info("Generating contract for: {}", employeeData.getEmployeeNameInEnglish());
                model.addAttribute("employeeData", employeeData);
                return "contract-template";
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

    // 11. Generate insurance paper for specific employee
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

    // 12. Download insurance PDF
    @GetMapping("/employees/download-insurance/{id}")
    public ResponseEntity<byte[]> downloadInsurancePDF(@PathVariable Long id) {
        try {
            logger.info("Generating insurance PDF for employee ID: {}", id);
            Optional<Employee> employee = employeeRepository.findById(id);
            
            if (employee.isPresent()) {
                EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
                
                byte[] pdfBytes = pdfService.fillInsuranceForm(employeeData);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", 
                    "insurance_" + employeeData.getEmployeeNameInEnglish() + ".pdf");
                headers.setCacheControl("no-cache, no-store, must-revalidate");
                headers.setPragma("no-cache");
                headers.setExpires(0);
                
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(pdfBytes);
            } else {
                logger.warn("Employee not found for PDF generation with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error generating insurance PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 13. View employee contract (read-only view)
    @GetMapping("/employees/view-contract/{id}")
    public String viewEmployeeContract(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Viewing employee contract with ID: {}", id);
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
                logger.info("Displaying contract view for: {}", employeeData.getEmployeeNameInEnglish());
                model.addAttribute("employee", employeeData);
                return "view-employee-contract";
            } else {
                logger.warn("Employee not found for contract view with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Employee not found with ID: " + id);
                return "redirect:/view-employees";
            }
        } catch (Exception e) {
            logger.error("Error loading employee contract: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error loading employee contract: " + e.getMessage());
            return "redirect:/view-employees";
        }
    }

    // 14. Download contract PDF - FIXED METHOD NAME
    @GetMapping("/employees/download-contract/{id}")
    public ResponseEntity<byte[]> downloadContractPDF(@PathVariable Long id) {
        try {
            logger.info("Generating contract PDF for employee ID: {}", id);
            Optional<Employee> employee = employeeRepository.findById(id);
            
            if (employee.isPresent()) {
                EmployeeData employeeData = EmployeeMapper.toDto(employee.get());
                
                // Use existing method or create a placeholder
                byte[] pdfBytes = generateContractPDF(employeeData);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", 
                    "contract_" + employeeData.getEmployeeNameInEnglish() + ".pdf");
                headers.setCacheControl("no-cache, no-store, must-revalidate");
                headers.setPragma("no-cache");
                headers.setExpires(0);
                
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(pdfBytes);
            } else {
                logger.warn("Employee not found for contract PDF generation with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error generating contract PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Placeholder method for contract PDF generation
    private byte[] generateContractPDF(EmployeeData employeeData) {
        try {
            // If PDFService has a method for contracts, use it here
            // For now, return empty bytes or throw exception
            logger.warn("Contract PDF generation not implemented yet for: {}", employeeData.getEmployeeNameInEnglish());
            return new byte[0];
        } catch (Exception e) {
            logger.error("Error in contract PDF generation: {}", e.getMessage());
            throw new RuntimeException("Contract PDF generation not implemented");
        }
    }
}