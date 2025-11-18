// Number to Text Conversion Functions

// Arabic number to words conversion
function convertNumberToArabicWords(number) {
    if (number === 0) return 'صفر';
    
    const ones = ['', 'واحد', 'اثنان', 'ثلاثة', 'أربعة', 'خمسة', 'ستة', 'سبعة', 'ثمانية', 'تسعة'];
    const teens = ['عشرة', 'أحد عشر', 'اثنا عشر', 'ثلاثة عشر', 'أربعة عشر', 'خمسة عشر', 'ستة عشر', 'سبعة عشر', 'ثمانية عشر', 'تسعة عشر'];
    const tens = ['', '', 'عشرون', 'ثلاثون', 'أربعون', 'خمسون', 'ستون', 'سبعون', 'ثمانون', 'تسعون'];
    const hundreds = ['', 'مائة', 'مئتان', 'ثلاثمائة', 'أربعمائة', 'خمسمائة', 'ستمائة', 'سبعمائة', 'ثمانمائة', 'تسعمائة'];
    
    if (number < 10) return ones[number];
    if (number < 20) return teens[number - 10];
    if (number < 100) {
        if (number % 10 === 0) return tens[Math.floor(number / 10)];
        return ones[number % 10] + ' و' + tens[Math.floor(number / 10)];
    }
    if (number < 1000) {
        const hundredsDigit = Math.floor(number / 100);
        const remainder = number % 100;
        let hundredText = hundreds[hundredsDigit];
        
        if (remainder === 0) {
            return hundredText;
        } else {
            return hundredText + ' و' + convertNumberToArabicWords(remainder);
        }
    }
    if (number < 1000000) {
        const thousands = Math.floor(number / 1000);
        const remainder = number % 1000;
        
        let thousandText = '';
        if (thousands === 1) {
            thousandText = 'ألف';
        } else if (thousands === 2) {
            thousandText = 'ألفان';
        } else if (thousands >= 3 && thousands <= 10) {
            thousandText = convertNumberToArabicWords(thousands) + ' ألاف';
        } else {
            thousandText = convertNumberToArabicWords(thousands) + ' ألف';
        }
        
        if (remainder === 0) {
            return thousandText;
        } else {
            return thousandText + ' و' + convertNumberToArabicWords(remainder);
        }
    }
    
    return 'مبلغ كبير';
}

// English number to words conversion
function convertNumberToEnglishWords(number) {
    if (number === 0) return 'Zero';
    
    const ones = ['', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine'];
    const teens = ['Ten', 'Eleven', 'Twelve', 'Thirteen', 'Fourteen', 'Fifteen', 'Sixteen', 'Seventeen', 'Eighteen', 'Nineteen'];
    const tens = ['', '', 'Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety'];
    const thousands = ['', 'Thousand', 'Million', 'Billion'];
    
    if (number < 0) return 'Negative ' + convertNumberToEnglishWords(-number);
    
    let words = '';
    
    for (let i = 0; number > 0; i++) {
        if (number % 1000 !== 0) {
            words = convertHundreds(number % 1000) + (thousands[i] ? ' ' + thousands[i] : '') + ' ' + words;
        }
        number = Math.floor(number / 1000);
    }
    
    return words.trim();
    
    function convertHundreds(num) {
        let result = '';
        
        // Hundreds
        if (num >= 100) {
            result += ones[Math.floor(num / 100)] + ' Hundred ';
            num %= 100;
        }
        
        // Tens and ones
        if (num >= 20) {
            result += tens[Math.floor(num / 10)] + ' ';
            num %= 10;
        }
        
        if (num >= 10) {
            result += teens[num - 10] + ' ';
            num = 0;
        }
        
        if (num > 0) {
            result += ones[num] + ' ';
        }
        
        return result;
    }
}

// Main salary words conversion
function updateSalaryWords(amount) {
    if (!amount || amount <= 0) {
        clearSalaryWords();
        return;
    }

    // English conversion
    const englishText = convertNumberToEnglishWords(Math.round(amount));
    document.getElementById('salaryInEnglishText').value = englishText + ' Egyptian Pounds Only';
    document.getElementById('salaryInEnglishText').classList.add('auto-translated');

    // Arabic conversion
    const arabicText = convertNumberToArabicWords(Math.round(amount));
    document.getElementById('salaryInArabicTextDisplay').value = 'فقط ' + arabicText + ' جنيهاً مصرياً لا غير';
    document.getElementById('salaryInArabicTextDisplay').classList.add('auto-translated');
}

function clearSalaryWords() {
    document.getElementById('salaryInEnglishText').value = '';
    document.getElementById('salaryInArabicTextDisplay').value = '';
    document.getElementById('salaryInEnglishText').classList.remove('auto-translated');
    document.getElementById('salaryInArabicTextDisplay').classList.remove('auto-translated');
}

// Variable salary conversion
function initializeVariableSalaryConversion() {
    const variableSalaryInput = document.getElementById('variableSalaryInNumber');
    
    if (variableSalaryInput) {
        let debounceTimer;
        variableSalaryInput.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                updateVariableSalaryWords(this.value);
            }, 500);
        });
    }
}

function updateVariableSalaryWords(amount) {
    const numericAmount = parseFloat(amount) || 0;
    
    const englishTextElement = document.getElementById('variableSalaryInEnglishText');
    const arabicTextElement = document.getElementById('variableSalaryInArabicText');
    
    if (!numericAmount || numericAmount <= 0) {
        englishTextElement.value = '';
        arabicTextElement.value = '';
        return;
    }

    // English conversion
    const englishText = convertNumberToEnglishWords(Math.round(numericAmount));
    englishTextElement.value = englishText + ' Egyptian Pounds Only';
    englishTextElement.classList.add('auto-translated');

    // Arabic conversion
    const arabicText = convertNumberToArabicWords(Math.round(numericAmount));
    arabicTextElement.value = 'فقط ' + arabicText + ' جنيهاً مصرياً لا غير';
    arabicTextElement.classList.add('auto-translated');
}

// Export functions globally
window.convertNumberToArabicWords = convertNumberToArabicWords;
window.convertNumberToEnglishWords = convertNumberToEnglishWords;
window.updateSalaryWords = updateSalaryWords;
window.clearSalaryWords = clearSalaryWords;
window.initializeVariableSalaryConversion = initializeVariableSalaryConversion;
window.updateVariableSalaryWords = updateVariableSalaryWords;