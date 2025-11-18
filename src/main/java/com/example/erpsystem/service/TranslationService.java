package com.example.erpsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TranslationService {
    
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Enhanced translation map
    private static final java.util.Map<String, String> TRANSLATION_MAP = java.util.Map.ofEntries(
        // Job titles
        java.util.Map.entry("manager", "مدير"),
        java.util.Map.entry("developer", "مطور"),
        java.util.Map.entry("engineer", "مهندس"),
        java.util.Map.entry("analyst", "محلل"),
        java.util.Map.entry("designer", "مصمم"),
        java.util.Map.entry("assistant", "مساعد"),
        java.util.Map.entry("specialist", "أخصائي"),
        java.util.Map.entry("coordinator", "منسق"),
        java.util.Map.entry("director", "مدير"),
        java.util.Map.entry("supervisor", "مشرف"),
        java.util.Map.entry("administrator", "مسؤول"),
        java.util.Map.entry("officer", "موظف"),
        java.util.Map.entry("executive", "تنفيذي"),
        java.util.Map.entry("consultant", "مستشار"),
        java.util.Map.entry("representative", "مندوب"),
        java.util.Map.entry("technician", "فني"),
        
        // Education
        java.util.Map.entry("bachelor", "بكالوريوس"),
        java.util.Map.entry("master", "ماجستير"),
        java.util.Map.entry("doctor", "دكتور"),
        java.util.Map.entry("phd", "دكتوراه"),
        java.util.Map.entry("doctorate", "دكتوراه"),
        java.util.Map.entry("diploma", "دبلوم"),
        java.util.Map.entry("degree", "درجة"),
        java.util.Map.entry("university", "جامعة"),
        java.util.Map.entry("college", "كلية"),
        java.util.Map.entry("institute", "معهد"),
        java.util.Map.entry("school", "مدرسة"),
        java.util.Map.entry("education", "تعليم"),
        java.util.Map.entry("training", "تدريب"),
        
        // Common words
        java.util.Map.entry("senior", "كبير"),
        java.util.Map.entry("junior", "مبتدئ"),
        java.util.Map.entry("lead", "رئيس"),
        java.util.Map.entry("head", "رئيس"),
        java.util.Map.entry("chief", "رئيس"),
        java.util.Map.entry("technical", "فني"),
        java.util.Map.entry("business", "أعمال"),
        java.util.Map.entry("marketing", "تسويق"),
        java.util.Map.entry("sales", "مبيعات"),
        java.util.Map.entry("customer", "عميل"),
        java.util.Map.entry("service", "خدمة"),
        java.util.Map.entry("support", "دعم"),
        java.util.Map.entry("quality", "جودة"),
        java.util.Map.entry("assurance", "تأكيد"),
        java.util.Map.entry("control", "مراقبة"),
        java.util.Map.entry("project", "مشروع"),
        java.util.Map.entry("product", "منتج"),
        java.util.Map.entry("software", "برمجيات"),
        java.util.Map.entry("hardware", "عتاد"),
        java.util.Map.entry("network", "شبكة"),
        java.util.Map.entry("system", "نظام"),
        java.util.Map.entry("information", "معلومات"),
        java.util.Map.entry("technology", "تكنولوجيا"),
        java.util.Map.entry("department", "قسم"),
        java.util.Map.entry("team", "فريق"),
        java.util.Map.entry("company", "شركة"),
        java.util.Map.entry("organization", "منظمة"),
        java.util.Map.entry("office", "مكتب")
    );
    
    public TranslationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Main translation method that tries external API first, then falls back to simple translation
     */
    public String translateToArabic(String englishText) {
        if (englishText == null || englishText.trim().isEmpty()) {
            return "";
        }
        
        // Try external API first
        String apiTranslation = translateWithAPI(englishText);
        if (!apiTranslation.isEmpty()) {
            logger.info("Successfully translated via API: {} -> {}", englishText, apiTranslation);
            return apiTranslation;
        }
        
        // Fallback to simple translation
        String simpleTranslation = simpleTranslateToArabic(englishText);
        if (!simpleTranslation.isEmpty()) {
            logger.info("Used simple translation: {} -> {}", englishText, simpleTranslation);
            return simpleTranslation;
        }
        
        logger.warn("No translation available for: {}", englishText);
        return "";
    }
    
    /**
     * Translate using MyMemory Translation API
     */
    private String translateWithAPI(String englishText) {
        try {
            String url = "https://api.mymemory.translated.net/get?q=" + 
                        java.net.URLEncoder.encode(englishText, "UTF-8") + 
                        "&langpair=en|ar";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            
            if (root.has("responseData") && root.get("responseData").has("translatedText")) {
                String translated = root.get("responseData").get("translatedText").asText();
                // Validate that we got a real translation (not the same text)
                if (!translated.equalsIgnoreCase(englishText) && !translated.trim().isEmpty()) {
                    return translated;
                }
            }
        } catch (Exception e) {
            logger.debug("API translation failed for '{}': {}", englishText, e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Enhanced simple translation with word-by-word replacement
     */
    public String simpleTranslateToArabic(String englishText) {
        if (englishText == null || englishText.trim().isEmpty()) {
            return "";
        }
        
        String lowerText = englishText.toLowerCase();
        StringBuilder translated = new StringBuilder();
        boolean translationFound = false;
        
        // Split into words and try to translate each
        String[] words = englishText.split("\\s+");
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (TRANSLATION_MAP.containsKey(cleanWord)) {
                translated.append(TRANSLATION_MAP.get(cleanWord)).append(" ");
                translationFound = true;
            } else {
                translated.append(word).append(" ");
            }
        }
        
        return translationFound ? translated.toString().trim() : "";
    }
    
    /**
     * Batch translation for multiple fields
     */
    public java.util.Map<String, String> translateMultiple(java.util.Map<String, String> englishFields) {
        java.util.Map<String, String> translations = new java.util.HashMap<>();
        
        for (java.util.Map.Entry<String, String> entry : englishFields.entrySet()) {
            String arabicText = translateToArabic(entry.getValue());
            translations.put(entry.getKey(), arabicText);
        }
        
        return translations;
    }
}