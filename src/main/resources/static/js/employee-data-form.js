// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('Initializing application...');
    
    // Initialize language tabs
    initializeLanguageTabs();
    
    // Initialize translation features
    if (typeof initializeTranslationFeatures === 'function') {
        initializeTranslationFeatures();
    }
    
    // Initialize salary conversion
    if (typeof initializeSalaryConversion === 'function') {
        initializeSalaryConversion();
    }

    // Initialize variable salary conversion
    initializeVariableSalaryConversion();
    
    // Initialize photo upload
    initializePhotoUpload();
    
    // Initialize progress tracking
    initializeProgressTracking();
    
    // Initialize automatic end date
    initializeAutoEndDate();
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize progress bar navigation
    initializeProgressBarNavigation();

    
    
    console.log('Application initialized successfully');
});

// Simple Progress Bar Navigation - Click to scroll to section
function initializeProgressBarNavigation() {
    // Make all steps clickable
    document.querySelectorAll('.step').forEach(step => {
        step.addEventListener('click', function() {
            const targetSection = this.getAttribute('data-section');
            scrollToSection(targetSection);
        });
    });
}

function scrollToSection(sectionId) {
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        // Smooth scroll to the section
        targetSection.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'start' 
        });
        
        // Update active step in progress bar
        updateActiveStep(sectionId);
        
        // Add temporary highlight to the section
        highlightSection(targetSection);
    }
}

function updateActiveStep(sectionId) {
    // Remove active class from all steps
    document.querySelectorAll('.step').forEach(step => {
        step.classList.remove('active');
    });
    
    // Add active class to current step
    const currentStep = document.querySelector(`[data-section="${sectionId}"]`);
    if (currentStep) {
        currentStep.classList.add('active');
    }
}

function highlightSection(sectionElement) {
    // Add highlight class
    sectionElement.classList.add('highlighted');
    
    // Remove highlight after 2 seconds
    setTimeout(() => {
        sectionElement.classList.remove('highlighted');
    }, 2000);
}

// Language tab functionality
function initializeLanguageTabs() {
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
}

// Photo Upload Functionality
function initializePhotoUpload() {
    const uploadArea = document.getElementById('uploadArea');
    const photoInput = document.getElementById('employeePhoto');
    const uploadPreview = uploadArea?.querySelector('.upload-preview');
    const uploadPlaceholder = uploadArea?.querySelector('.upload-placeholder');
    
    if (!uploadArea || !photoInput) return;
    
    // Click to upload
    uploadArea.addEventListener('click', function() {
        photoInput.click();
    });
    
    // Drag and drop functionality
    uploadArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });
    
    uploadArea.addEventListener('dragleave', function() {
        uploadArea.classList.remove('dragover');
    });
    
    uploadArea.addEventListener('drop', function(e) {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFile(files[0]);
        }
    });
    
    // File input change
    photoInput.addEventListener('change', function(e) {
        if (e.target.files.length > 0) {
            handleFile(e.target.files[0]);
        }
    });
    
    function handleFile(file) {
        if (!file.type.startsWith('image/')) {
            alert('Please select an image file (JPG, PNG)');
            return;
        }
        
        if (file.size > 5 * 1024 * 1024) {
            alert('File size must be less than 5MB');
            return;
        }
        
        const reader = new FileReader();
        reader.onload = function(e) {
            const previewImg = document.getElementById('photoPreview');
            previewImg.src = e.target.result;
            
            uploadPlaceholder.classList.add('d-none');
            uploadPreview.classList.remove('d-none');
        };
        reader.readAsDataURL(file);
    }
}

function changePhoto() {
    document.getElementById('employeePhoto').click();
}

function removePhoto() {
    const photoInput = document.getElementById('employeePhoto');
    const uploadPreview = document.querySelector('.upload-preview');
    const uploadPlaceholder = document.querySelector('.upload-placeholder');
    
    photoInput.value = '';
    uploadPreview.classList.add('d-none');
    uploadPlaceholder.classList.remove('d-none');
}

// Progress Tracking
function initializeProgressTracking() {
    const formFields = document.querySelectorAll('input, select, textarea');
    
    formFields.forEach(field => {
        field.addEventListener('input', updateProgress);
        field.addEventListener('change', updateProgress);
    });
    
    updateProgress();
}

function updateProgress() {
    const sections = [
        { id: 'section-contract', fields: ['startDate', 'endDate'] },
        { id: 'section-company', fields: ['companyNameInEnglish'] },
        { id: 'section-employee', fields: ['employeeNameInEnglish', 'titleInEnglish', 'nationalId'] },
        { id: 'section-salary', fields: ['contributionSalary'] }
    ];
    
    let completedSections = 0;
    
    sections.forEach((section, index) => {
        const sectionCompleted = checkSectionCompletion(section.fields);
        
        if (sectionCompleted) {
            completedSections++;
            updateStepIndicator(index + 1, true);
        } else {
            updateStepIndicator(index + 1, false);
        }
    });
    
    const totalProgress = (completedSections / sections.length) * 100;
    const progressBar = document.getElementById('progressBar');
    if (progressBar) {
        progressBar.style.width = totalProgress + '%';
        progressBar.setAttribute('aria-valuenow', totalProgress);
    }
}

function checkSectionCompletion(fieldNames) {
    for (const fieldName of fieldNames) {
        let field = document.querySelector(`[name="${fieldName}"]`);
        if (!field) {
            field = document.getElementById(fieldName);
        }
        
        if (!field || !isFieldValid(field)) {
            return false;
        }
    }
    return true;
}

function isFieldValid(field) {
    const value = field.value ? field.value.trim() : '';
    
    if (field.hasAttribute('required') && value === '') {
        return false;
    }
    
    return value !== '';
}

function updateStepIndicator(stepNumber, isCompleted) {
    const stepElement = document.getElementById('step' + stepNumber);
    
    if (stepElement) {
        if (isCompleted) {
            stepElement.classList.add('completed');
            stepElement.classList.remove('active');
        } else {
            stepElement.classList.remove('completed', 'active');
        }
    }
    
    updateActiveStep();
}

function updateActiveStep() {
    const allSteps = document.querySelectorAll('.step');
    allSteps.forEach(step => step.classList.remove('active'));
    
    const sections = [
        { step: 1, fields: ['startDate', 'endDate'] },
        { step: 2, fields: ['companyNameInEnglish'] },
        { step: 3, fields: ['employeeNameInEnglish', 'titleInEnglish', 'nationalId'] },
        { step: 4, fields: ['contributionSalary'] }
    ];
    
    let foundActive = false;
    for (const section of sections) {
        const isCompleted = checkSectionCompletion(section.fields);
        
        if (!isCompleted && !foundActive) {
            const stepElement = document.getElementById('step' + section.step);
            if (stepElement) {
                stepElement.classList.add('active');
                foundActive = true;
            }
        }
    }
    
    if (!foundActive) {
        const lastStep = document.getElementById('step' + sections.length);
        if (lastStep) {
            lastStep.classList.add('active');
        }
    }
}

// Automatic end date calculation
function initializeAutoEndDate() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    
    if (startDateInput && endDateInput) {
        startDateInput.addEventListener('change', function() {
            const calculatedEndDate = calculateEndDate(this.value);
            if (calculatedEndDate) {
                endDateInput.value = calculatedEndDate;
            }
        });
        
        startDateInput.addEventListener('input', function() {
            const calculatedEndDate = calculateEndDate(this.value);
            if (calculatedEndDate) {
                endDateInput.value = calculatedEndDate;
            }
        });
        
        endDateInput.title = "This field is automatically calculated based on the start date";
    }
}

function calculateEndDate(startDateValue) {
    if (!startDateValue) return '';
    
    const startDate = new Date(startDateValue);
    const endDate = new Date(startDate);
    endDate.setFullYear(endDate.getFullYear() + 1);
    endDate.setDate(endDate.getDate() - 1);
    
    return endDate.toISOString().split('T')[0];
}

// Form validation
function initializeFormValidation() {
    const contractForm = document.getElementById('contractForm');
    if (!contractForm) return;
    
    contractForm.addEventListener('submit', function(e) {
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
        
        // Additional validation for dates
        const startDate = document.getElementById('startDate')?.value;
        const endDate = document.getElementById('endDate')?.value;
        
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            
            if (end <= start) {
                isValid = false;
                alert('End date must be after start date');
            }
        }
        
        if (!isValid) {
            e.preventDefault();
            alert('Please fill in all required fields marked with *');
        }
    });
}

// Export functions that might be called from HTML
window.changePhoto = changePhoto;
window.removePhoto = removePhoto;