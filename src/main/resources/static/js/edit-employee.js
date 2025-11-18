// Language tab functionality
document.querySelectorAll('.language-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        const target = tab.getAttribute('data-target');
        
        // Remove active class from all tabs and content
        document.querySelectorAll('.language-tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.language-content').forEach(c => c.classList.remove('active'));
        
        // Add active class to clicked tab and corresponding content
        tab.classList.add('active');
        document.getElementById(target).classList.add('active');
    });
});

// Form validation
document.getElementById('editForm').addEventListener('submit', function(e) {
    let isValid = true;
    const requiredFields = this.querySelectorAll('[required]');
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            isValid = false;
            field.classList.add('is-invalid');
        } else {
            field.classList.remove('is-invalid');
        }
    });
    
    if (!isValid) {
        e.preventDefault();
        alert('Please fill in all required fields marked with *');
    }
});


        document.addEventListener('DOMContentLoaded', function() {
            initializeFormFeatures();
            setupProgressTracker();
            setupTranslationFeatures();
            setupPhotoUpload();
        });

        function initializeFormFeatures() {
            // Language tabs functionality
            const languageTabs = document.querySelectorAll('.language-tab');
            
            languageTabs.forEach(tab => {
                tab.addEventListener('click', function() {
                    const targetId = this.getAttribute('data-target');
                    const parentSection = this.closest('.form-section');
                    
                    // Update active tab
                    const tabs = parentSection.querySelectorAll('.language-tab');
                    tabs.forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                    
                    // Show/hide language content
                    const contents = parentSection.querySelectorAll('.language-content');
                    contents.forEach(content => {
                        content.classList.remove('active');
                        if (content.id === targetId) {
                            content.classList.add('active');
                        }
                    });
                });
            });

            // Form validation
            const form = document.getElementById('editForm');
            if (form) {
                form.addEventListener('submit', function(e) {
                    const requiredFields = form.querySelectorAll('[required]');
                    let isValid = true;
                    
                    requiredFields.forEach(field => {
                        if (!field.value.trim()) {
                            isValid = false;
                            field.classList.add('is-invalid');
                        } else {
                            field.classList.remove('is-invalid');
                        }
                    });
                    
                    if (!isValid) {
                        e.preventDefault();
                        alert('Please fill in all required fields.');
                    }
                });
            }
        }

        function setupProgressTracker() {
            const progressBar = document.getElementById('progressBar');
            const steps = document.querySelectorAll('.step');
            const sections = document.querySelectorAll('.form-section');
            const progressFields = document.querySelectorAll('.progress-field');

            function updateProgress() {
                let filledFields = 0;
                progressFields.forEach(field => {
                    if (field.value.trim() !== '') {
                        filledFields++;
                    }
                });

                const progress = (filledFields / progressFields.length) * 100;
                progressBar.style.width = `${progress}%`;
                progressBar.setAttribute('aria-valuenow', progress);

                // Update step indicators
                const sectionProgress = Math.floor((progress / 100) * steps.length);
                steps.forEach((step, index) => {
                    step.classList.remove('active', 'completed');
                    if (index < sectionProgress) {
                        step.classList.add('completed');
                    } else if (index === sectionProgress) {
                        step.classList.add('active');
                    }
                });
            }

            // Update progress on field changes
            progressFields.forEach(field => {
                field.addEventListener('input', updateProgress);
                field.addEventListener('change', updateProgress);
            });

            // Initial progress update
            updateProgress();

            // Step click navigation
            steps.forEach((step, index) => {
                step.addEventListener('click', () => {
                    if (index < sections.length) {
                        sections[index].scrollIntoView({ behavior: 'smooth' });
                    }
                });
            });
        }

        function setupTranslationFeatures() {
            const autoTranslateToggle = document.getElementById('autoTranslateToggle');
            const translateAllBtn = document.getElementById('translateAllBtn');
            const englishFields = document.querySelectorAll('.english-field');
            const arabicFields = document.querySelectorAll('.arabic-field');

            // Auto-translate functionality
            if (autoTranslateToggle) {
                autoTranslateToggle.addEventListener('change', function() {
                    document.body.classList.toggle('auto-translate-enabled', this.checked);
                    
                    if (this.checked) {
                        // Enable real-time translation
                        englishFields.forEach(field => {
                            field.addEventListener('input', handleAutoTranslation);
                        });
                    } else {
                        // Disable real-time translation
                        englishFields.forEach(field => {
                            field.removeEventListener('input', handleAutoTranslation);
                        });
                    }
                });
            }

            // Translate all button
            if (translateAllBtn) {
                translateAllBtn.addEventListener('click', function() {
                    translateAllBtn.disabled = true;
                    translateAllBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Translating...';
                    
                    setTimeout(() => {
                        englishFields.forEach(field => {
                            const arabicField = document.getElementById(field.id.replace('English', 'Arabic'));
                            if (arabicField) {
                                arabicField.value = simulateTranslation(field.value);
                                arabicField.classList.add('auto-translated');
                            }
                        });
                        
                        translateAllBtn.disabled = false;
                        translateAllBtn.innerHTML = '<i class="fas fa-language me-1"></i>Translate All Now';
                        
                        showToast('All fields translated successfully!', 'success');
                    }, 1000);
                });
            }

            function handleAutoTranslation(e) {
                const englishField = e.target;
                const arabicField = document.getElementById(englishField.id.replace('English', 'Arabic'));
                
                if (arabicField && !arabicField.classList.contains('manually-edited')) {
                    arabicField.value = simulateTranslation(englishField.value);
                    arabicField.classList.add('auto-translated');
                }
            }

            // Track manual edits to Arabic fields
            arabicFields.forEach(field => {
                field.addEventListener('input', function() {
                    this.classList.remove('auto-translated');
                    this.classList.add('manually-edited');
                });
            });

            function simulateTranslation(text) {
                // This is a simulation - in a real app, you would call a translation API
                if (!text) return '';
                
                // Simple simulation for demo purposes
                const translations = {
                    'developer': 'مطور',
                    'senior': 'أول',
                    'manager': 'مدير',
                    'engineer': 'مهندس',
                    'analyst': 'محلل',
                    'director': 'مدير',
                    'coordinator': 'منسق',
                    'specialist': 'أخصائي',
                    'assistant': 'مساعد',
                    'officer': 'ضابط'
                };
                
                let translated = text;
                Object.keys(translations).forEach(key => {
                    const regex = new RegExp(key, 'gi');
                    translated = translated.replace(regex, translations[key]);
                });
                
                return translated ? `(${translated})` : '';
            }
        }

        function setupPhotoUpload() {
            const uploadArea = document.getElementById('uploadArea');
            const fileInput = document.getElementById('employeePhoto');
            const preview = document.querySelector('.upload-preview');
            const placeholder = document.querySelector('.upload-placeholder');
            const previewImage = document.getElementById('photoPreview');

            if (uploadArea && fileInput) {
                // Click to upload
                uploadArea.addEventListener('click', () => fileInput.click());

                // Drag and drop
                uploadArea.addEventListener('dragover', (e) => {
                    e.preventDefault();
                    uploadArea.classList.add('dragover');
                });

                uploadArea.addEventListener('dragleave', () => {
                    uploadArea.classList.remove('dragover');
                });

                uploadArea.addEventListener('drop', (e) => {
                    e.preventDefault();
                    uploadArea.classList.remove('dragover');
                    const files = e.dataTransfer.files;
                    if (files.length > 0) {
                        handleFileSelect(files[0]);
                    }
                });

                // File input change
                fileInput.addEventListener('change', (e) => {
                    if (e.target.files.length > 0) {
                        handleFileSelect(e.target.files[0]);
                    }
                });
            }

            function handleFileSelect(file) {
                if (file && file.type.startsWith('image/')) {
                    if (file.size > 5 * 1024 * 1024) {
                        showToast('File size must be less than 5MB', 'error');
                        return;
                    }

                    const reader = new FileReader();
                    reader.onload = (e) => {
                        previewImage.src = e.target.result;
                        placeholder.classList.add('d-none');
                        preview.classList.remove('d-none');
                    };
                    reader.readAsDataURL(file);
                } else {
                    showToast('Please select a valid image file', 'error');
                }
            }

            window.changePhoto = function() {
                fileInput.click();
            };

            window.removePhoto = function() {
                fileInput.value = '';
                preview.classList.add('d-none');
                placeholder.classList.remove('d-none');
            };
        }

        function showToast(message, type = 'info') {
            // Simple toast notification implementation
            const toast = document.createElement('div');
            toast.className = `alert alert-${type} translation-toast position-fixed`;
            toast.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
            toast.innerHTML = `
                <div class="d-flex align-items-center">
                    <i class="fas fa-${getIcon(type)} me-2"></i>
                    <div>${message}</div>
                    <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
                </div>
            `;
            document.body.appendChild(toast);
            
            setTimeout(() => {
                if (toast.parentElement) {
                    toast.remove();
                }
            }, 5000);
        }

        function getIcon(type) {
            const icons = {
                success: 'check-circle',
                error: 'exclamation-circle',
                warning: 'exclamation-triangle',
                info: 'info-circle'
            };
            return icons[type] || 'info-circle';
        }
