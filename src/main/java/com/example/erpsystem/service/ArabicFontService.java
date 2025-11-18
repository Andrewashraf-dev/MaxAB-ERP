package com.example.erpsystem.service;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.itextpdf.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ArabicFontService {
   
    private BaseFont arabicBaseFont;
    private boolean fontLoaded = false;
   
    // Prioritize fonts that support Arabic letter connection
    private static final String[] FONT_PATHS = {
         "fonts/arialbd.ttf",              // Arial Bold - First priority
        "fonts/arial.ttf",                // Arial Regular - Fallback
        "fonts/Amiri-Bold.ttf",           // Amiri Bold
        "fonts/Amiri-Regular.ttf",
        "fonts/NotoNaskhArabic-Bold.ttf",
        "fonts/NotoNaskhArabic-Regular.ttf",
        "fonts/Scheherazade-Bold.ttf",
        "fonts/Scheherazade-Regular.ttf"
    };
    
    public ArabicFontService() {
        loadArabicFont();
    }
   
    private void loadArabicFont() {
        System.out.println("=== Loading Arabic Font with ICU4J Support ===");
       
        for (String fontPath : FONT_PATHS) {
            if (tryLoadFont(fontPath)) {
                return;
            }
        }
       
        System.err.println("❌ CRITICAL: No Arabic font could be loaded!");
        System.err.println("❌ Please ensure you have at least one Arabic font in src/main/resources/fonts/");
        System.err.println("❌ Recommended: Download Amiri-Regular.ttf from https://fonts.google.com/specimen/Amiri");
    }
   
    private boolean tryLoadFont(String fontPath) {
        try {
            ClassPathResource fontResource = new ClassPathResource(fontPath);
           
            if (!fontResource.exists()) {
                System.out.println("❌ Font not found: " + fontPath);
                return false;
            }
           
            System.out.println("✅ Found font: " + fontPath);
           
            try (InputStream fontStream = fontResource.getInputStream()) {
                byte[] fontData = fontStream.readAllBytes();
               
                // CRITICAL: Use IDENTITY_H for proper Unicode support
                this.arabicBaseFont = BaseFont.createFont(
                    fontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true,
                    fontData,
                    null
                );
               
                fontLoaded = true;
                System.out.println("🎉 SUCCESS: Arabic font loaded!");
                System.out.println("   Font: " + fontPath);
                System.out.println("   Encoding: " + arabicBaseFont.getEncoding());
                
                // Test with actual Arabic text
                testArabicRendering();
                return true;
            }
           
        } catch (Exception e) {
            System.err.println("❌ Failed to load " + fontPath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
   
    private void testArabicRendering() {
        try {
            String testText = "محمد";
            System.out.println("\n🧪 Testing Arabic rendering:");
            System.out.println("   Input text: " + testText);
            System.out.println("   Input bytes: " + bytesToHex(testText.getBytes(StandardCharsets.UTF_8)));
            
            String shaped = shapeArabicText(testText);
            System.out.println("   Shaped text length: " + shaped.length());
            System.out.println("   Shaped bytes: " + bytesToHex(shaped.getBytes(StandardCharsets.UTF_8)));
            
            float width = arabicBaseFont.getWidthPoint(shaped, 12);
            System.out.println("   Text width at 12pt: " + width);
            System.out.println("✅ Arabic rendering test completed\n");
           
        } catch (Exception e) {
            System.err.println("❌ Arabic rendering test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, 20); i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString();
    }
   
    /**
     * CRITICAL: Shape Arabic text using ICU4J for proper letter connection
     */
    public String shapeArabicText(String arabicText) {
        if (arabicText == null || arabicText.isEmpty()) {
            return arabicText;
        }
        
        // Check if text contains Arabic characters
        if (!containsArabic(arabicText)) {
            System.out.println("⚠️ Text doesn't contain Arabic characters: " + arabicText);
            return arabicText;
        }
        
        try {
            System.out.println("🔄 Shaping Arabic text with ICU4J:");
            System.out.println("   Input: '" + arabicText + "' (length: " + arabicText.length() + ")");
            
            // Step 1: Shape Arabic letters (connect them properly)
            ArabicShaping shaper = new ArabicShaping(
                ArabicShaping.LETTERS_SHAPE | 
                ArabicShaping.LENGTH_GROW_SHRINK
            );
            String shaped = shaper.shape(arabicText);
            System.out.println("   After shaping: length = " + shaped.length());
            
            // Step 2: Apply bidirectional reordering (RTL)
            // Remove OUTPUT_REVERSE since the text is already in visual order
            Bidi bidi = new Bidi(shaped, Bidi.DIRECTION_RIGHT_TO_LEFT);
            String reordered = bidi.writeReordered(Bidi.DO_MIRRORING);
            
            System.out.println("   Final output: length = " + reordered.length());
            System.out.println("✅ Arabic text shaped successfully");
            
            return reordered;
            
        } catch (ArabicShapingException e) {
            System.err.println("❌ Arabic shaping error: " + e.getMessage());
            e.printStackTrace();
            return arabicText;
        } catch (Exception e) {
            System.err.println("❌ Error processing Arabic text: " + e.getMessage());
            e.printStackTrace();
            return arabicText;
        }
    }
    
    /**
     * Check if text contains Arabic characters
     */
    private boolean containsArabic(String text) {
        for (char c : text.toCharArray()) {
            if (c >= 0x0600 && c <= 0x06FF) { // Arabic Unicode block
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get shaped Arabic text ready for PDF rendering
     */
    public String getShapedText(String arabicText) {
        return shapeArabicText(arabicText);
    }
   
    public BaseFont getArabicBaseFont() {
        if (!fontLoaded) {
            System.err.println("⚠️ WARNING: Arabic font not loaded, text may not display correctly");
        }
        return arabicBaseFont;
    }
   
    public boolean isArabicFontLoaded() {
        return fontLoaded && arabicBaseFont != null;
    }
}