// Translation functionality
let autoTranslateEnabled = true;
let translationTimeout;

// Enhanced translation functionality
function initializeTranslationFeatures() {
    console.log('Initializing translation features...');
    
    const autoTranslateToggle = document.getElementById('autoTranslateToggle');
    const translateAllBtn = document.getElementById('translateAllBtn');
    
    if (autoTranslateToggle) {
        autoTranslateToggle.checked = true;
        updateTranslationUIState();
        
        autoTranslateToggle.addEventListener('change', function() {
            autoTranslateEnabled = this.checked;
            showToast(
                autoTranslateEnabled ? 'Auto-translation enabled' : 'Auto-translation disabled',
                autoTranslateEnabled ? 'success' : 'info'
            );
            updateTranslationUIState();
        });
    }

    if (translateAllBtn) {
        translateAllBtn.addEventListener('click', translateAllFields);
    }

    // Auto-translate on input for English fields
    const englishFields = document.querySelectorAll('.english-field');
    if (englishFields.length > 0) {
        englishFields.forEach(field => {
            field.addEventListener('input', function() {
                if (autoTranslateEnabled) {
                    clearTimeout(translationTimeout);
                    translationTimeout = setTimeout(() => {
                        const targetId = this.id.replace('English', 'Arabic');
                        translateField(this.id, targetId);
                    }, 1000);
                }
            });
            
            field.addEventListener('focus', function() {
                this.classList.add('field-focused');
            });
            
            field.addEventListener('blur', function() {
                this.classList.remove('field-focused');
            });
        });
    }
    
    // Manual edit detection for Arabic fields
    const arabicFields = document.querySelectorAll('.arabic-field');
    arabicFields.forEach(field => {
        field.addEventListener('input', function() {
            this.classList.add('manually-edited');
            showToast('Manual edit detected - auto-translation disabled for this field', 'warning', 2000);
        });
    });
}

// Translate all English fields at once using batch API
async function translateAllFields() {
    const englishFields = [
        'employeeNameInEnglish',
        'titleInEnglish', 
        'educationInEnglish',
        'addressInEnglish'
    ];
    
    const translateAllBtn = document.getElementById('translateAllBtn');
    if (!translateAllBtn) return;
    
    const originalText = translateAllBtn.innerHTML;
    
    // Show loading state
    translateAllBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Translating...';
    translateAllBtn.disabled = true;
    
    try {
        // Prepare data for batch translation
        const translationData = {};
        let hasContent = false;
        
        for (const fieldId of englishFields) {
            const sourceField = document.getElementById(fieldId);
            if (sourceField && sourceField.value.trim()) {
                translationData[fieldId] = sourceField.value.trim();
                hasContent = true;
            }
        }
        
        if (!hasContent) {
            showToast('No English text found to translate', 'warning');
            return;
        }
        
        // Use batch translation API
        const response = await fetch('/api/translation/batch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(translationData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        
        if (result.success) {
            const translations = result.translations;
            let successCount = 0;
            
            // Apply translations to corresponding Arabic fields
            for (const [fieldId, arabicText] of Object.entries(translations)) {
                if (arabicText && arabicText.trim()) {
                    const targetId = fieldId.replace('English', 'Arabic');
                    const targetField = document.getElementById(targetId);
                    
                    if (targetField && !targetField.classList.contains('manually-edited')) {
                        targetField.value = arabicText;
                        targetField.classList.add('auto-translated');
                        successCount++;
                    }
                }
            }
            
            showToast(`Successfully translated ${successCount} fields`, 'success');
        } else {
            throw new Error(result.error || 'Batch translation failed');
        }
        
    } catch (error) {
        console.error('Batch translation error:', error);
        showToast('Translation failed - using fallback method', 'error');
        // Fallback to individual translation
        await translateAllFieldsIndividually();
    } finally {
        // Restore button state
        translateAllBtn.innerHTML = originalText;
        translateAllBtn.disabled = false;
    }
}

// Enhanced translation function using backend service
async function translateField(sourceId, targetId, showNotification = true) {
    const sourceField = document.getElementById(sourceId);
    const targetField = document.getElementById(targetId);
    
    if (!sourceField || !targetField) {
        console.error('Source or target field not found:', sourceId, targetId);
        return;
    }
    
    const textToTranslate = sourceField.value.trim();
    if (!textToTranslate) {
        targetField.value = '';
        return;
    }

    // Skip if target field was manually edited
    if (targetField.classList.contains('manually-edited')) {
        if (showNotification) {
            showToast('Field was manually edited - skipping auto-translation', 'info', 2000);
        }
        return;
    }

    // Show loading state
    targetField.classList.add('translating');
    const originalValue = targetField.value;
    targetField.value = '...';

    try {
        // Use backend translation service
        const response = await fetch('/api/translation/to-arabic', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ text: textToTranslate })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        
        if (result.success && result.translatedText) {
            targetField.value = result.translatedText;
            targetField.classList.add('auto-translated');
            
            if (showNotification) {
                showToast('Translation completed', 'success', 1500);
            }
        } else {
            throw new Error(result.error || 'Translation failed');
        }
        
    } catch (error) {
        console.error('Translation error:', error);
        // Keep original value on error
        targetField.value = originalValue;
        if (showNotification) {
            showToast('Translation failed - please enter Arabic text manually', 'error');
        }
    } finally {
        targetField.classList.remove('translating');
    }
}

// Fallback individual translation
async function translateAllFieldsIndividually() {
    const englishFields = [
        'employeeNameInEnglish',
        'titleInEnglish', 
        'educationInEnglish',
        'addressInEnglish'
    ];
    
    let successCount = 0;
    const totalFields = englishFields.length;
    
    for (const fieldId of englishFields) {
        const sourceField = document.getElementById(fieldId);
        if (sourceField && sourceField.value.trim()) {
            const targetId = fieldId.replace('English', 'Arabic');
            try {
                await translateField(fieldId, targetId, false);
                successCount++;
            } catch (error) {
                console.error(`Translation failed for ${fieldId}:`, error);
            }
        }
    }
    
    if (successCount > 0) {
        showToast(`Translated ${successCount} out of ${totalFields} fields`, 'info');
    }
}

function updateTranslationUIState() {
    const translateAllBtn = document.getElementById('translateAllBtn');
    
    if (autoTranslateEnabled) {
        document.body.classList.add('auto-translate-enabled');
        if (translateAllBtn) {
            translateAllBtn.classList.remove('btn-outline-primary', 'btn-success');
            translateAllBtn.classList.add('btn-outline-success');
        }
    } else {
        document.body.classList.remove('auto-translate-enabled');
        if (translateAllBtn) {
            translateAllBtn.classList.remove('btn-outline-success', 'btn-success');
            translateAllBtn.classList.add('btn-outline-primary');
        }
    }
}

// Enhanced toast notification
function showToast(message, type = 'info', duration = 3000) {
    // Remove existing toasts to prevent stacking
    const existingToasts = document.querySelectorAll('.translation-toast');
    existingToasts.forEach(toast => toast.remove());
    
    const toast = document.createElement('div');
    toast.className = `translation-toast alert alert-${type} alert-dismissible fade show`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        max-width: 400px;
        backdrop-filter: blur(10px);
        border: none;
    `;
    
    const icons = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };
    
    toast.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="fas ${icons[type] || 'fa-info-circle'} me-2"></i>
            <div class="flex-grow-1">${message}</div>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    document.body.appendChild(toast);
    
    // Auto-remove after duration
    setTimeout(() => {
        if (toast.parentNode) {
            toast.remove();
        }
    }, duration);
}

// Export functions that need to be accessed globally
window.initializeTranslationFeatures = initializeTranslationFeatures;
window.translateAllFields = translateAllFields;
window.translateField = translateField;
window.showToast = showToast;