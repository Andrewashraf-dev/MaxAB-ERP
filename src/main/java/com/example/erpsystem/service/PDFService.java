package com.example.erpsystem.service;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.example.erpsystem.dto.EmployeeData;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class PDFService {

    public byte[] fillInsuranceForm(EmployeeData employeeData) throws Exception {
        // تحميل نموذج PDF الموجود
        ClassPathResource templateResource = new ClassPathResource("templates/insurance-form.pdf");
        PdfReader reader = new PdfReader(templateResource.getInputStream());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        
        AcroFields form = stamper.getAcroFields();
        
        // ملء الحقول في الPDF
        Map<String, String> fieldValues = prepareFieldValues(employeeData);
        
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            if (form.getField(entry.getKey()) != null) {
                form.setField(entry.getKey(), entry.getValue());
            }
        }
        
        stamper.setFormFlattening(true); // لجعل الPDF غير قابل للتعديل بعد الملء
        stamper.close();
        reader.close();
        
        return baos.toByteArray();
    }
    
    private Map<String, String> prepareFieldValues(EmployeeData employeeData) {
        Map<String, String> fieldValues = new HashMap<>();
        
        // تعيين القيم للحقول في الPDF
        fieldValues.put("employeeNameInEnglish", employeeData.getEmployeeNameInEnglish());
        fieldValues.put("employeeNameInArabic", employeeData.getEmployeeNameInArabic());
        fieldValues.put("nationalId", employeeData.getNationalId());
        fieldValues.put("insuranceNumber", employeeData.getInsuranceNumber());
        fieldValues.put("companyName", employeeData.getCompanyNameInEnglish());
        fieldValues.put("companyNameArabic", employeeData.getCompanyNameInArabic());
        fieldValues.put("jobTitle", employeeData.getTitleInEnglish());
        fieldValues.put("jobTitleArabic", employeeData.getTitleInArabic());
        fieldValues.put("startDate", employeeData.getStartDate());
        fieldValues.put("endDate", employeeData.getEndDate());
        fieldValues.put("basicSalary", employeeData.getBasicSalaryInEnglish());
        fieldValues.put("basicSalaryArabic", employeeData.getBasicSalaryInArabic());
        fieldValues.put("address", employeeData.getAddressInEnglish());
        fieldValues.put("addressArabic", employeeData.getAddressInArabic());
        
        return fieldValues;
    }
}