package com.example.erpsystem.controller;

import com.example.erpsystem.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/translation")
@CrossOrigin(origins = "*")
public class TranslationController {
    
    @Autowired
    private TranslationService translationService;
    
    @PostMapping("/to-arabic")
    public ResponseEntity<Map<String, String>> translateToArabic(@RequestBody Map<String, String> request) {
        String englishText = request.get("text");
        
        if (englishText == null || englishText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
        }
        
        try {
            String arabicText = translationService.translateToArabic(englishText);
            return ResponseEntity.ok(Map.of(
                "translatedText", arabicText,
                "originalText", englishText,
                "success", "true"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Translation failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> translateBatch(@RequestBody Map<String, String> request) {
        try {
            Map<String, String> translations = translationService.translateMultiple(request);
            return ResponseEntity.ok(Map.of(
                "translations", translations,
                "success", "true",
                "translatedCount", translations.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Batch translation failed: " + e.getMessage(),
                "success", "false"
            ));
        }
    }
}