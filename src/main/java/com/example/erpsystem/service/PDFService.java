package com.example.erpsystem.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.example.erpsystem.dto.EmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class PDFService {

    @Autowired
    private NumberBoxRenderer numberBoxRenderer;

    public byte[] fillInsuranceForm(EmployeeData employeeData) throws Exception {
        System.out.println("=== Starting PDF Generation ===");

        verifyArabicFont();
        
        // Load PDF template
        ClassPathResource templateResource = new ClassPathResource("templates/insurance1.pdf");
        
        System.out.println("Loading PDF template from: templates/insurance1.pdf");
        System.out.println("Template exists: " + templateResource.exists());
        
        if (!templateResource.exists()) {
            System.out.println("ERROR: PDF template not found!");
            throw new Exception("PDF template not found: templates/insurance1.pdf");
        }
        
        PdfReader reader = new PdfReader(templateResource.getInputStream());
        System.out.println("PDF reader created successfully");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        
        AcroFields form = stamper.getAcroFields();
        
        // Log available fields for debugging
        Set<String> fields = form.getFields().keySet();
        System.out.println("Available PDF form fields: " + fields);
        
        // Fill form fields
        Map<String, String> fieldValues = prepareFieldValues(employeeData);
        
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            if (shouldSkipField(entry.getKey())) {
                System.out.println("🚫 SKIPPING field '" + entry.getKey() + "' - will be replaced with boxes");
                continue;
            }
            
            if (form.getField(entry.getKey()) != null) {
                System.out.println("✅ FILLING field '" + entry.getKey() + "' with value: '" + entry.getValue() + "'");
                form.setField(entry.getKey(), entry.getValue());
            } else {
                System.out.println("⚠️ WARNING: Field '" + entry.getKey() + "' not found in PDF template");
            }
        }
        
        // Add number boxes
        addNumberBoxes(stamper, employeeData);
        
        // ✅ CRITICAL: Add Arabic text directly to PDF
        addArabicText(stamper, employeeData);
        
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        
        System.out.println("PDF generated successfully with " + baos.size() + " bytes");
        System.out.println("=== PDF Generation Completed ===");
        
        return baos.toByteArray();
    }
    
    /**
     * Determine which fields to skip (they will be replaced with boxes)
     */
    private boolean shouldSkipField(String fieldName) {
        return fieldName.equals("nationalId") || 
               fieldName.equals("insuranceNumber") || 
               fieldName.equals("CompanyInsuranceNumber") ||
               fieldName.equals("companyInsuranceNumber") ||
               fieldName.equals("basicSalaryInEnglish") ||
               fieldName.equals("ContributionSalary") ||
               fieldName.equals("TitleCode") ||
               fieldName.equals("startDate");
    }
    
    /**
     * Add number boxes for all numeric fields
     */
    private void addNumberBoxes(PdfStamper stamper, EmployeeData employeeData) {
        try {
            System.out.println("=== Adding Number Boxes ===");
            System.out.println("Employee Data: " + employeeData.getEmployeeNameInEnglish());
        
            // National ID (14 digits)
            if (employeeData.getNationalId() != null && !employeeData.getNationalId().isEmpty()) {
                System.out.println("Drawing nationalId in boxes: " + employeeData.getNationalId());
                numberBoxRenderer.drawProfessionalBoxes(stamper, employeeData.getNationalId(), 275f, 550f, "nationalId", 0.6f);
            }
            
            // Insurance Number (7 digits)
            if (employeeData.getInsuranceNumber() != null && !employeeData.getInsuranceNumber().isEmpty()) {
                System.out.println("Drawing insuranceNumber in boxes: " + employeeData.getInsuranceNumber());
                numberBoxRenderer.drawProfessionalBoxes(stamper, employeeData.getInsuranceNumber(), 365f, 575f, "insuranceNumber", 0.6f);
            }
            
            // Company Insurance Number (6 digits)
            if (employeeData.getCompanyInsuranceNumber() != null && !employeeData.getCompanyInsuranceNumber().isEmpty()) {
                System.out.println("Drawing companyInsuranceNumber in boxes: " + employeeData.getCompanyInsuranceNumber());
                numberBoxRenderer.drawProfessionalBoxes(stamper, employeeData.getCompanyInsuranceNumber(), 400f, 665f, "companyInsuranceNumber", 0.6f);
            }

            // Company Insurance Number (6 digits)
            if (employeeData.getCompanyInsuranceNumber() != null && !employeeData.getCompanyInsuranceNumber().isEmpty()) {
                System.out.println("Drawing companyInsuranceNumber in boxes: " + employeeData.getCompanyInsuranceNumber());
                numberBoxRenderer.drawProfessionalBoxes(stamper, employeeData.getCompanyInsuranceNumber(), 400f, 665f, "companyInsuranceNumber", 0.6f);
            }
           
            // Basic Salary (numeric) - 7 boxes always
            if (employeeData.getBasicSalaryInEnglish() != null && !employeeData.getBasicSalaryInEnglish().isEmpty()) {
                System.out.println("Drawing basicSalary in boxes: " + employeeData.getBasicSalaryInEnglish());
                numberBoxRenderer.drawSalaryInBoxes(stamper, employeeData.getBasicSalaryInEnglish(), 5f, 387f, "basicSalary");
            }
            
            // Contribution Salary (numeric) - 7 boxes always
            if (employeeData.getContributionSalary() != null && !employeeData.getContributionSalary().isEmpty()) {
                System.out.println("Drawing contributionSalary in boxes: " + employeeData.getContributionSalary());
                numberBoxRenderer.drawSalaryInBoxes(stamper, employeeData.getContributionSalary(), 191.5f, 387.5f, "contributionSalary");
            }
            
            // Title Code with slash (6 digits with / in middle)
            if (employeeData.getJobTitleCode() != null && !employeeData.getJobTitleCode().isEmpty()) {
                System.out.println("Drawing TitleCode with slash: " + employeeData.getJobTitleCode());
                numberBoxRenderer.drawNumberWithSlashSeparator(stamper, employeeData.getJobTitleCode(), 246f, 492f, "TitleCode");
            }
            
            // Start Date with slashes (YYYY-MM-DD format)
            if (employeeData.getStartDate() != null && !employeeData.getStartDate().isEmpty()) {
                System.out.println("Drawing startDate in boxes: " + employeeData.getStartDate());
                numberBoxRenderer.drawDateInBoxes(stamper, employeeData.getStartDate(), 300f, 462f, "startDate");
            }
            
            System.out.println("=== Number Boxes Addition Completed ===");
            
        } catch (Exception e) {
            System.err.println("Error adding number boxes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Add Arabic text directly to PDF using coordinates
     */
    private void addArabicText(PdfStamper stamper, EmployeeData employeeData) {
        try {
            System.out.println("=== Adding Arabic Text Directly to PDF ===");
            
            // Draw all Arabic text fields
            numberBoxRenderer.drawAllArabicText(stamper, employeeData);
            
            System.out.println("=== Arabic Text Addition Completed ===");
            
            
            // Draw duplicated Arabic text on second page
            numberBoxRenderer.drawArabicTextOnSecondPage(stamper, employeeData);

        } catch (Exception e) {
            System.err.println("Error adding Arabic text: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   /**
 * Prepare field values - EXCLUDE ARABIC TEXT FIELDS (they will be drawn directly)
 */
private Map<String, String> prepareFieldValues(EmployeeData employeeData) {
    Map<String, String> fieldValues = new HashMap<>();
    
    // English fields only (Arabic fields will be drawn directly)
    fieldValues.put("Title", getValueOrEmpty(employeeData.getTitleInEnglish()));
    fieldValues.put("address", getValueOrEmpty(employeeData.getAddressInEnglish()));
    fieldValues.put("endDate", getValueOrEmpty(employeeData.getEndDate()));
    
    // DO NOT include Arabic fields here - they will be drawn directly via canvas
    
    // Numeric fields (will be replaced with boxes)
    fieldValues.put("nationalId", getValueOrEmpty(employeeData.getNationalId()));
    fieldValues.put("insuranceNumber", getValueOrEmpty(employeeData.getInsuranceNumber()));
    fieldValues.put("CompanyInsuranceNumber", getValueOrEmpty(employeeData.getCompanyInsuranceNumber()));
    fieldValues.put("companyInsuranceNumber", getValueOrEmpty(employeeData.getCompanyInsuranceNumber()));
    fieldValues.put("basicSalaryInEnglish", getValueOrEmpty(employeeData.getBasicSalaryInEnglish()));
    fieldValues.put("ContributionSalary", getValueOrEmpty(employeeData.getContributionSalary()));
    fieldValues.put("TitleCode", getValueOrEmpty(employeeData.getJobTitleCode()));
    fieldValues.put("startDate", getValueOrEmpty(employeeData.getStartDate()));
    
    System.out.println("📝 Prepared field values for PDF (Arabic fields excluded): " + fieldValues);
    return fieldValues;
}
    
    private String getValueOrEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * Helper method to generate a PDF with coordinate grid for testing
     */
    public byte[] generateTestPDFWithGrid() throws Exception {
        ClassPathResource templateResource = new ClassPathResource("templates/insurance1.pdf");
        PdfReader reader = new PdfReader(templateResource.getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        
        PdfContentByte canvas = stamper.getOverContent(1);
        
        // Draw coordinate grid
        canvas.setColorStroke(BaseColor.RED);
        canvas.setLineWidth(0.5f);
        
        // Draw horizontal lines every 50 units
        for (int y = 0; y < 800; y += 50) {
            canvas.moveTo(0, y);
            canvas.lineTo(600, y);
            canvas.stroke();
            
            // Add Y coordinate labels
            canvas.beginText();
            canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED), 8);
            canvas.setColorFill(BaseColor.RED);
            canvas.setTextMatrix(5, y + 2);
            canvas.showText("Y=" + y);
            canvas.endText();
        }
        
        // Draw vertical lines every 50 units
        for (int x = 0; x < 600; x += 50) {
            canvas.moveTo(x, 0);
            canvas.lineTo(x, 800);
            canvas.stroke();
            
            // Add X coordinate labels
            canvas.beginText();
            canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED), 8);
            canvas.setColorFill(BaseColor.RED);
            canvas.setTextMatrix(x + 2, 10);
            canvas.showText("X=" + x);
            canvas.endText();
        }
        
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        
        return baos.toByteArray();
    }
   /**
 * Verify Arabic font is properly loaded before generating PDF
 */
private void verifyArabicFont() {
    try {
        // Direct access to ArabicFontService
        if (numberBoxRenderer != null) {
            java.lang.reflect.Field fontServiceField = numberBoxRenderer.getClass().getDeclaredField("arabicFontService");
            fontServiceField.setAccessible(true);
            ArabicFontService fontService = (ArabicFontService) fontServiceField.get(numberBoxRenderer);
            
            if (fontService.isArabicFontLoaded()) {
                System.out.println("✅ Arabic font verification: PASSED");
                System.out.println("✅ Font encoding: " + fontService.getArabicBaseFont().getEncoding());
            } else {
                System.err.println("❌ Arabic font verification: FAILED - Arabic text will not display correctly");
            }
        }
    } catch (Exception e) {
        System.err.println("⚠️ Could not verify Arabic font: " + e.getMessage());
    }
}
}