package com.example.erpsystem.service;

import com.example.erpsystem.dto.EmployeeData;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NumberBoxRenderer {

    // Constants for consistent styling
    private static final float BOX_WIDTH = 16.5f;
    private static final float BOX_HEIGHT = 21f;
    private static final float GAP = 0.2f;
    private static final float LINE_THICKNESS = 0.6f;
    private static final float FONT_SIZE = 12f;

    @Autowired
    private ArabicFontService arabicFontService;

    /**
     * Professional method with customizable line thickness and height
     */
    public void drawProfessionalBoxes(PdfStamper stamper, String number, 
                                    float startX, float startY, String fieldName,
                                    float lineThickness) throws Exception {
        PdfContentByte canvas = stamper.getOverContent(1);
        
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        
        char[] digits = number.toCharArray();
        
        System.out.println("🔲 Drawing " + fieldName + ": " + number + " at [" + startX + "," + startY + "] with thickness: " + lineThickness);
        
        for (int i = 0; i < digits.length; i++) {
            float x = startX + (i * (BOX_WIDTH + GAP));
            float y = startY;
            
            // Save the graphics state
            canvas.saveState();
            
            // Draw filled white rectangle
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(x, y, BOX_WIDTH, BOX_HEIGHT);
            canvas.fill();
            
            // Draw black border WITH CUSTOM THICKNESS
            canvas.setColorStroke(BaseColor.BLACK);
            canvas.setLineWidth(lineThickness);
            canvas.rectangle(x, y, BOX_WIDTH, BOX_HEIGHT);
            canvas.stroke();
            
            // Restore graphics state before text
            canvas.restoreState();
            
            // Draw the digit
            canvas.beginText();
            canvas.setFontAndSize(baseFont, FONT_SIZE);
            canvas.setColorFill(BaseColor.BLACK);
            
            String digit = String.valueOf(digits[i]);
            float textWidth = baseFont.getWidthPoint(digit, FONT_SIZE);
            float textX = x + (BOX_WIDTH - textWidth) / 2;
            float textY = y + (BOX_HEIGHT - FONT_SIZE) / 2 + 3;
            
            canvas.setTextMatrix(textX, textY);
            canvas.showText(digit);
            canvas.endText();
        }
        
        System.out.println("✅ Completed " + fieldName);
    }

    /**
     * Draw numbers with slash separator in the middle (for TitleCode: 123/456)
     */
    public void drawNumberWithSlashSeparator(PdfStamper stamper, String number, 
                                           float startX, float startY, String fieldName) throws Exception {
        PdfContentByte canvas = stamper.getOverContent(1);
        
        float slashWidth = 10f;
        
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        
        // Ensure we have exactly 6 digits
        String paddedNumber = String.format("%06d", Integer.parseInt(number));
        char[] digits = paddedNumber.toCharArray();
        
        System.out.println("🔲 Drawing " + fieldName + " with slash: " + paddedNumber);
        
        // Draw first 3 digits (before slash)
        for (int i = 0; i < 3; i++) {
            float x = startX + (i * (BOX_WIDTH + GAP));
            float y = startY;
            
            drawSingleBoxWithNumber(canvas, baseFont, String.valueOf(digits[i]), x, y);
        }
        
        // Draw slash in the middle
        float slashX = startX + (3 * (BOX_WIDTH + GAP)) + 2;
        float slashY = startY + BOX_HEIGHT / 2 - 2;
        
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 14);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.setTextMatrix(slashX, slashY);
        canvas.showText("/");
        canvas.endText();
        
        // Draw last 3 digits (after slash)
        for (int i = 3; i < 6; i++) {
            float x = startX + (i * (BOX_WIDTH + GAP)) + slashWidth;
            float y = startY;
            
            drawSingleBoxWithNumber(canvas, baseFont, String.valueOf(digits[i]), x, y);
        }
    }

    /**
     * Draw date in format YYYY-MM-DD with boxes and slashes
     */
    public void drawDateInBoxes(PdfStamper stamper, String date, 
                               float startX, float startY, String fieldName) throws Exception {
        PdfContentByte canvas = stamper.getOverContent(1);
        
        float slashWidth = 8f;
        
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        
        System.out.println("📅 Drawing " + fieldName + ": " + date);
        
        // Parse date (assuming format: YYYY-MM-DD)
        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            System.out.println("❌ Invalid date format: " + date);
            return;
        }
        
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        
        float currentX = startX;
        
        // Draw YEAR (4 digits)
        for (int i = 0; i < 4; i++) {
            drawSingleBoxWithNumber(canvas, baseFont, String.valueOf(year.charAt(i)), currentX, startY);
            currentX += BOX_WIDTH + GAP;
        }
        
        // Draw first slash
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 14);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.setTextMatrix(currentX + 2, startY + BOX_HEIGHT / 2 - 2);
        canvas.showText("/");
        canvas.endText();
        
        currentX += slashWidth;
        
        // Draw MONTH (2 digits)
        for (int i = 0; i < 2; i++) {
            drawSingleBoxWithNumber(canvas, baseFont, String.valueOf(month.charAt(i)), currentX, startY);
            currentX += BOX_WIDTH + GAP;
        }
        
        // Draw second slash
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 14);
        canvas.setColorFill(BaseColor.BLACK);
        canvas.setTextMatrix(currentX + 2, startY + BOX_HEIGHT / 2 - 2);
        canvas.showText("/");
        canvas.endText();
        
        currentX += slashWidth;
        
        // Draw DAY (2 digits)
        for (int i = 0; i < 2; i++) {
            drawSingleBoxWithNumber(canvas, baseFont, String.valueOf(day.charAt(i)), currentX, startY);
            currentX += BOX_WIDTH + GAP;
        }
    }

    /**
     * Helper method to draw a single box with number
     */
    private void drawSingleBoxWithNumber(PdfContentByte canvas, BaseFont baseFont, 
                                       String digit, float x, float y) throws Exception {
        // Draw filled white rectangle
        canvas.setColorFill(BaseColor.WHITE);
        canvas.rectangle(x, y, BOX_WIDTH, BOX_HEIGHT);
        canvas.fill();
        
        // Draw black border
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.setLineWidth(LINE_THICKNESS);
        canvas.rectangle(x, y, BOX_WIDTH, BOX_HEIGHT);
        canvas.stroke();
        
        // Draw the digit
        canvas.beginText();
        canvas.setFontAndSize(baseFont, FONT_SIZE);
        canvas.setColorFill(BaseColor.BLACK);
        
        float textWidth = baseFont.getWidthPoint(digit, FONT_SIZE);
        float textX = x + (BOX_WIDTH - textWidth) / 2;
        float textY = y + (BOX_HEIGHT - FONT_SIZE) / 2 + 3;
        
        canvas.setTextMatrix(textX, textY);
        canvas.showText(digit);
        canvas.endText();
    }

    /**
     * Draw salary amounts with exactly 7 boxes
     */
    public void drawSalaryInBoxes(PdfStamper stamper, String salary, 
                                 float startX, float startY, String fieldName) throws Exception {
        PdfContentByte canvas = stamper.getOverContent(1);
        
        int totalBoxes = 7;
        
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        
        System.out.println("💰 Drawing " + fieldName + ": " + salary + " (always " + totalBoxes + " boxes)");
        
        // Handle decimal numbers
        String cleanSalary = salary;
        if (salary.contains(".")) {
            cleanSalary = salary.substring(0, salary.indexOf('.'));
        }
        
        char[] digits = cleanSalary.toCharArray();
        
        // Always draw exactly 7 boxes
        for (int i = 0; i < totalBoxes; i++) {
            float x = startX + (i * (BOX_WIDTH + GAP));
            float y = startY;
            
            // Draw the box
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(x, y, BOX_WIDTH, BOX_HEIGHT);
            canvas.fill();
            
            canvas.setColorStroke(BaseColor.BLACK);
            canvas.setLineWidth(LINE_THICKNESS);
            canvas.rectangle(x, y, BOX_WIDTH, BOX_HEIGHT);
            canvas.stroke();
            
            // Only draw digit if it exists
            if (i < digits.length) {
                canvas.beginText();
                canvas.setFontAndSize(baseFont, FONT_SIZE);
                canvas.setColorFill(BaseColor.BLACK);
                
                String digit = String.valueOf(digits[i]);
                float textWidth = baseFont.getWidthPoint(digit, FONT_SIZE);
                float textX = x + (BOX_WIDTH - textWidth) / 2;
                float textY = y + (BOX_HEIGHT - FONT_SIZE) / 2 + 3;
                
                canvas.setTextMatrix(textX, textY);
                canvas.showText(digit);
                canvas.endText();
                
                System.out.println("   Box " + (i+1) + ": '" + digit + "'");
            } else {
                System.out.println("   Box " + (i+1) + ": EMPTY");
            }
        }
    }

    /**
     * Draw all Arabic text fields with precise coordinates on FIRST PAGE
     */
    public void drawAllArabicText(PdfStamper stamper, EmployeeData employeeData) throws Exception {
        System.out.println("=== Drawing Arabic Text Fields on Page 1 ===");
        
        PdfContentByte canvas = stamper.getOverContent(1);
        BaseFont arabicFont = arabicFontService.getArabicBaseFont();
        
        if (arabicFont == null) {
            System.err.println("❌ CRITICAL: Arabic font is null! Cannot draw Arabic text.");
            return;
        }
        
        // Company Name in Arabic - FIRST PAGE
        if (employeeData.getCompanyNameInArabic() != null && !employeeData.getCompanyNameInArabic().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getCompanyNameInArabic(), 
                                   480f, 650f, 14f, "companyNameInArabic - Page 1");
        }   
        
        // Employee Name in Arabic
        if (employeeData.getEmployeeNameInArabic() != null && !employeeData.getEmployeeNameInArabic().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getEmployeeNameInArabic(), 
                                    410f, 525f, 14f, "employeeNameInArabic");
        }
    
        // Title in Arabic
        if (employeeData.getTitleInArabic() != null && !employeeData.getTitleInArabic().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getTitleInArabic(), 
                                   80f, 495f , 12f, "TitleArabic");
        }
        
        // Education in Arabic
        if (employeeData.getEducationInArabic() != null && !employeeData.getEducationInArabic().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getEducationInArabic(), 
                                     485f, 495f, 13f, "educationInArabic");
        }
        
        // Address in Arabic
        if (employeeData.getAddressInArabic() != null && !employeeData.getAddressInArabic().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getAddressInArabic(), 
                                    100f, 620f, 12f, "addressArabic");
        }

        // Company Tax Number
        if (employeeData.getCompanyTaxNumber() != null && !employeeData.getCompanyTaxNumber().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getCompanyTaxNumber(), 
                                    60f, 650f, 11f, "companyTaxNumber");
        }
        
        System.out.println("=== Arabic Text Drawing Completed for Page 1 ===");
    }

    /**
     * Draw Arabic text on SECOND page (for duplication)
     */
    public void drawArabicTextOnSecondPage(PdfStamper stamper, EmployeeData employeeData) throws Exception {
        System.out.println("=== Drawing Arabic Text Fields on Page 2 ===");
        
        // Check if PDF has at least 2 pages
        int totalPages = stamper.getReader().getNumberOfPages();
        if (totalPages < 2) {
            System.out.println("⚠️ PDF has only " + totalPages + " page(s). Cannot draw on page 2.");
            return;
        }
        
        PdfContentByte canvas = stamper.getOverContent(2); // Use page 2
        BaseFont arabicFont = arabicFontService.getArabicBaseFont();
        
        if (arabicFont == null) {
            System.err.println("❌ CRITICAL: Arabic font is null! Cannot draw Arabic text on page 2.");
            return;
        }
        
        // Company Name in Arabic - SECOND PAGE (duplicated)
        if (employeeData.getCompanyNameInArabic() != null && !employeeData.getCompanyNameInArabic().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getCompanyNameInArabic(), 
                                   480f, 650f, 14f, "companyNameInArabic - Page 2");
        }
        
        // You can add other fields that need to be duplicated on page 2 here
        // For example, if you need company tax number on the second page:
        if (employeeData.getCompanyTaxNumber() != null && !employeeData.getCompanyTaxNumber().isEmpty()) {
            drawArabicTextAtPosition(canvas, arabicFont, employeeData.getCompanyTaxNumber(), 
                                    60f, 650f, 11f, "companyTaxNumber - Page 2");
        }
        
        System.out.println("=== Arabic Text Drawing Completed for Page 2 ===");
    }

    /**
     * Draw Arabic text at specific position with proper shaping
     */
    private void drawArabicTextAtPosition(PdfContentByte canvas, BaseFont arabicFont, 
                                         String arabicText, float x, float y, 
                                         float fontSize, String fieldName) {
        try {
            System.out.println("📝 Drawing " + fieldName + ":");
            System.out.println("   Original text: '" + arabicText + "'");
            
            // CRITICAL: Shape the text using ICU4J
            String shapedText = arabicFontService.shapeArabicText(arabicText);
            
            System.out.println("   Shaped text length: " + shapedText.length());
            System.out.println("   Position: [" + x + ", " + y + "]");
            System.out.println("   Font size: " + fontSize);
            
            // Draw the shaped text
            canvas.saveState();
            canvas.beginText();
            canvas.setFontAndSize(arabicFont, fontSize);
            canvas.setColorFill(BaseColor.BLACK);
            canvas.setTextMatrix(x, y);
            canvas.showText(shapedText);
            canvas.endText();
            canvas.restoreState();
            
            System.out.println("✅ Successfully drew Arabic text for: " + fieldName);
            
        } catch (Exception e) {
            System.err.println("❌ Error drawing Arabic text for " + fieldName + ": " + e.getMessage());
            e.printStackTrace();
            
            // Draw error indicator
            try {
                canvas.saveState();
                canvas.setColorStroke(BaseColor.RED);
                canvas.setLineWidth(1f);
                canvas.rectangle(x, y, 200, 20);
                canvas.stroke();
                canvas.restoreState();
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    /**
     * Draw Arabic text with custom font size (backward compatibility)
     */
    public void drawArabicText(PdfStamper stamper, String arabicText, 
                              float x, float y, float fontSize, String fieldName) throws Exception {
        PdfContentByte canvas = stamper.getOverContent(1);
        BaseFont arabicFont = arabicFontService.getArabicBaseFont();
        
        if (arabicFont == null) {
            System.err.println("❌ Arabic font is null for: " + fieldName);
            return;
        }
        
        drawArabicTextAtPosition(canvas, arabicFont, arabicText, x, y, fontSize, fieldName);
    }

    /**
     * Draw Arabic text with default font size (backward compatibility)
     */
    public void drawArabicText(PdfStamper stamper, String arabicText, 
                              float x, float y, String fieldName) throws Exception {
        drawArabicText(stamper, arabicText, x, y, 12f, fieldName);
    }
}