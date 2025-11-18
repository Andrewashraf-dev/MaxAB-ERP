// Salary Conversion Functionality
let currentConversionType = 'gross-to-net';

function initializeSalaryConversion() {
    // Conversion toggle
    document.querySelectorAll('.conversion-option').forEach(option => {
        option.addEventListener('click', function() {
            const type = this.getAttribute('data-type');
            switchConversionType(type);
        });
    });

    // Gross to Net calculation
    const grossSalaryInput = document.getElementById('grossSalary');
    if (grossSalaryInput) {
        let debounceTimer;
        grossSalaryInput.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                // Auto-calculate contribution salary first
                calculateContributionSalary();
                
                if (currentConversionType === 'gross-to-net') {
                    calculateGrossToNet();
                }
            }, 800);
        });
    }

    // Net to Gross calculation
    const netSalaryInput = document.getElementById('netSalaryInput');
    if (netSalaryInput) {
        let debounceTimer;
        netSalaryInput.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                if (currentConversionType === 'net-to-gross') {
                    calculateNetToGross();
                }
            }, 800);
        });
    }

    // Update contribution salary help text
    updateContributionHelpText();
}

// Automatic Contribution Salary Calculation
function calculateContributionSalary() {
    const grossSalaryInput = document.getElementById('grossSalary');
    const contributionSalaryInput = document.getElementById('contributionSalary');
    
    if (!grossSalaryInput || !contributionSalaryInput) return;
    
    const grossSalary = parseFloat(grossSalaryInput.value) || 0;
    
    let contributionSalary;
    if (grossSalary < 14500) {
        contributionSalary = grossSalary;
    } else {
        contributionSalary = 14500;
    }
    
    // Set the contribution salary value
    contributionSalaryInput.value = contributionSalary.toFixed(2);
    
    // Trigger any dependent events (like progress tracking)
    contributionSalaryInput.dispatchEvent(new Event('input', { bubbles: true }));
    contributionSalaryInput.dispatchEvent(new Event('change', { bubbles: true }));
    
    // Update help text
    updateContributionHelpText();
}

// Helper function to calculate contribution from gross (without updating the field)
function calculateContributionFromGross(grossSalary) {
    if (grossSalary < 14500) {
        return grossSalary;
    } else {
        return 14500;
    }
}

// Helper function to calculate and set contribution salary from gross
function calculateContributionSalaryFromGross(grossSalary) {
    const contributionSalaryInput = document.getElementById('contributionSalary');
    if (!contributionSalaryInput) {
        console.error('Contribution salary input not found!');
        return;
    }
    
    let contributionSalary;
    if (grossSalary < 14500) {
        contributionSalary = grossSalary;
    } else {
        contributionSalary = 14500;
    }
    
    console.log('Setting contribution salary to:', contributionSalary, 'based on gross:', grossSalary);
    
    // Set the contribution salary value
    contributionSalaryInput.value = contributionSalary.toFixed(2);
    
    // Trigger events to update progress tracking, etc.
    contributionSalaryInput.dispatchEvent(new Event('input', { bubbles: true }));
    contributionSalaryInput.dispatchEvent(new Event('change', { bubbles: true }));
    
    // Update help text
    updateContributionHelpText();
}

function switchConversionType(type) {
    currentConversionType = type;
    
    // Update toggle buttons
    document.querySelectorAll('.conversion-option').forEach(option => {
        option.classList.remove('active');
    });
    document.querySelector(`[data-type="${type}"]`).classList.add('active');
    
    // Show/hide conversion content
    document.querySelectorAll('.conversion-content').forEach(content => {
        content.classList.add('d-none');
    });
    document.getElementById(`${type}-content`).classList.remove('d-none');
    
    // Clear results when switching
    if (type === 'gross-to-net') {
        document.getElementById('netSalaryAmount').textContent = '0.00';
        document.getElementById('grossSalary').value = '';
    } else {
        document.getElementById('grossSalaryAmount').textContent = '0.00';
        document.getElementById('netSalaryInput').value = '';
    }
    
    clearSalaryWords();
}

async function calculateGrossToNet() {
    const grossSalaryInput = document.getElementById('grossSalary');
    const netSalaryAmount = document.getElementById('netSalaryAmount');
    const contributionSalary = document.getElementById('contributionSalary');
    const calculatingAlert = document.getElementById('salaryCalculatingAlert');
    const errorAlert = document.getElementById('salaryErrorAlert');
    const errorMessage = document.getElementById('salaryErrorMessage');

    const grossSalary = parseFloat(grossSalaryInput.value);
    const contributionValue = parseFloat(contributionSalary.value) || 0;

    hideSalaryAlerts();

    if (!grossSalary || grossSalary <= 0) {
        netSalaryAmount.textContent = '0.00';
        clearSalaryWords();
        return;
    }

    // Auto-calculate contribution salary if not set or invalid
    if (!contributionValue || contributionValue <= 0) {
        calculateContributionSalary();
    }

    const updatedContributionValue = parseFloat(contributionSalary.value) || 0;

    if (!updatedContributionValue || updatedContributionValue <= 0) {
        showSalaryError('Please enter contribution salary first');
        netSalaryAmount.textContent = '0.00';
        clearSalaryWords();
        return;
    }

    if (calculatingAlert) {
        calculatingAlert.classList.remove('d-none');
    }

    try {
        const response = await fetch('/api/salary/calculate-net-salary', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                grossSalary: grossSalary,
                contributionSalary: updatedContributionValue
            })
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const result = await response.json();

        if (result.status === 'success') {
            const netSalary = result.netSalary;
            netSalaryAmount.textContent = netSalary.toFixed(2);
            hideSalaryAlerts();
            
            // Update salary in words (this function is now in numbertotext.js)
            if (typeof updateSalaryWords === 'function') {
                updateSalaryWords(grossSalary);
            }
            
        } else {
            showSalaryError(result.error || 'Calculation failed');
            netSalaryAmount.textContent = '0.00';
            clearSalaryWords();
        }
    } catch (error) {
        console.error('Salary calculation error:', error);
        showSalaryError('Network error: Unable to calculate salary');
        netSalaryAmount.textContent = '0.00';
        clearSalaryWords();
    } finally {
        if (calculatingAlert) {
            calculatingAlert.classList.add('d-none');
        }
    }
}

async function calculateNetToGross() {
    const netSalaryInput = document.getElementById('netSalaryInput');
    const grossSalaryAmount = document.getElementById('grossSalaryAmount');
    const calculatingAlert = document.getElementById('salaryCalculatingAlert');
    const errorAlert = document.getElementById('salaryErrorAlert');
    const errorMessage = document.getElementById('salaryErrorMessage');

    const netSalary = parseFloat(netSalaryInput.value);

    hideSalaryAlerts();

    if (!netSalary || netSalary <= 0) {
        grossSalaryAmount.textContent = '0.00';
        clearSalaryWords();
        // Also clear contribution salary
        document.getElementById('contributionSalary').value = '';
        return;
    }

    if (calculatingAlert) {
        calculatingAlert.classList.remove('d-none');
    }

    try {
        // Use iterative approach to find gross salary from net salary
        const estimatedGross = estimateGrossFromNet(netSalary);
        
        grossSalaryAmount.textContent = estimatedGross.toFixed(2);
        
        // AUTO-CALCULATE AND UPDATE CONTRIBUTION SALARY BASED ON GROSS
        calculateContributionSalaryFromGross(estimatedGross);
        
        hideSalaryAlerts();
        
        // Update salary in words (this function is now in numbertotext.js)
        if (typeof updateSalaryWords === 'function') {
            updateSalaryWords(estimatedGross);
        }
        
    } catch (error) {
        console.error('Salary calculation error:', error);
        showSalaryError('Error calculating gross salary');
        grossSalaryAmount.textContent = '0.00';
        document.getElementById('contributionSalary').value = '';
        clearSalaryWords();
    } finally {
        if (calculatingAlert) {
            calculatingAlert.classList.add('d-none');
        }
    }
}

function estimateGrossFromNet(netSalary) {
    let grossGuess = netSalary * 1.5;
    let tolerance = 0.01;
    let maxIterations = 50;
    
    for (let i = 0; i < maxIterations; i++) {
        // Calculate contribution based on current gross guess
        const contributionSalary = calculateContributionFromGross(grossGuess);
        
        const currentNet = calculateNetFromGross(grossGuess, contributionSalary);
        const error = currentNet - netSalary;
        
        if (Math.abs(error) < tolerance) {
            return grossGuess;
        }
        
        // Estimate derivative
        const grossStep = grossGuess * 0.001;
        const contributionAtHigher = calculateContributionFromGross(grossGuess + grossStep);
        const netAtHigher = calculateNetFromGross(grossGuess + grossStep, contributionAtHigher);
        const derivative = (netAtHigher - currentNet) / grossStep;
        
        if (Math.abs(derivative) < 0.0001) {
            return binarySearchFallback(netSalary);
        }
        
        grossGuess = grossGuess - error / derivative;
        
        // Ensure gross is reasonable
        if (grossGuess < netSalary) grossGuess = netSalary * 1.1;
        if (grossGuess > netSalary * 5) grossGuess = netSalary * 3;
    }
    
    return binarySearchFallback(netSalary);
}

function binarySearchFallback(netSalary) {
    let low = netSalary;
    let high = netSalary * 3;
    let guess = (low + high) / 2;
    
    for (let i = 0; i < 50; i++) {
        const contributionSalary = calculateContributionFromGross(guess);
        const calculatedNet = calculateNetFromGross(guess, contributionSalary);
        const difference = calculatedNet - netSalary;
        
        if (Math.abs(difference) < 0.01) return guess;
        
        if (difference > 0) high = guess;
        else low = guess;
        
        guess = (low + high) / 2;
    }
    return guess;
}

function calculateNetFromGross(grossSalary, contributionSalary) {
    // Company Share SOC Insurance = Contribution Salary * 0.1875
    const companyShareSocInsurance = contributionSalary * 0.1875;
    
    // Employee Share SOC Insurance = Contribution Salary * 0.11
    const employeeShareSocInsurance = contributionSalary * 0.11;
    
    // Martyrs Fund = Gross Salary * 0.0005
    const martyrsFund = grossSalary * 0.0005;
    
    // ORIGINAL WORKING FORMULA: Annual Tax Pools = ((Gross Salary - Employee Share SOC Insurance) * 12 - 20000)
    const annualTaxPools = ((grossSalary - employeeShareSocInsurance) * 12) - 20000;
    
    // Taxes (Basic) based on annual tax pools
    const taxesBasic = calculateTaxesBasic(annualTaxPools) / 12;
    
    // Net Salary = Gross Salary - Employee Share SOC Insurance - Taxes Basic - Martyrs Fund
    const netSalary = grossSalary - employeeShareSocInsurance - taxesBasic - martyrsFund;
    
    return netSalary;
}

function calculateTaxesBasic(annualTaxPools) {
    if (annualTaxPools <= 0) {
        return 0.0;
    } else if (annualTaxPools <= 40000) {
        return 0.0;
    } else if (annualTaxPools <= 55000) {
        return (annualTaxPools - 40000) * 0.10;
    } else if (annualTaxPools <= 70000) {
        return ((annualTaxPools - 55000) * 0.15) + 1500;
    } else if (annualTaxPools <= 200000) {
        return ((annualTaxPools - 70000) * 0.20) + 1500 + 2250;
    } else if (annualTaxPools <= 400000) {
        return ((annualTaxPools - 200000) * 0.225) + 1500 + 2250 + 26000;
    } else if (annualTaxPools <= 600000) {
        return ((annualTaxPools - 400000) * 0.25) + 1500 + 2250 + 26000 + 45000;
    } else if (annualTaxPools <= 700000) {
        return ((annualTaxPools - 400000) * 0.25) + 5500 + 2250 + 26000 + 45000;
    } else if (annualTaxPools <= 800000) {
        return ((annualTaxPools - 400000) * 0.25) + 10500 + 26000 + 45000;
    } else if (annualTaxPools <= 900000) {
        return ((annualTaxPools - 400000) * 0.25) + 40000 + 45000;
    } else if (annualTaxPools <= 1200000) {
        return ((annualTaxPools - 400000) * 0.25) + 90000;
    } else {
        return ((annualTaxPools - 1200000) * 0.275) + 300000;
    }
}

function updateContributionHelpText() {
    const grossSalaryInput = document.getElementById('grossSalary');
    const netSalaryInput = document.getElementById('netSalaryInput');
    const grossSalaryAmount = document.getElementById('grossSalaryAmount');
    const contributionSalary = document.getElementById('contributionSalary');
    const contributionHelp = document.getElementById('contributionHelp');
    
    if (contributionSalary && contributionHelp) {
        let grossValue;
        
        // Determine which gross salary to use based on current conversion type
        if (currentConversionType === 'gross-to-net' && grossSalaryInput) {
            grossValue = parseFloat(grossSalaryInput.value) || 0;
        } else if (currentConversionType === 'net-to-gross' && netSalaryInput && netSalaryInput.value) {
            // For net-to-gross, we need to estimate the gross
            const netSalary = parseFloat(netSalaryInput.value) || 0;
            grossValue = estimateGrossFromNet(netSalary);
        } else {
            grossValue = parseFloat(contributionSalary.value) || 0;
        }
        
        if (grossValue < 14500) {
            contributionHelp.textContent = `Using ${grossValue.toLocaleString()} as contribution salary since gross is less than 14,500`;
            contributionHelp.className = 'text-success';
        } else {
            contributionHelp.textContent = 'Using maximum 14,500 as contribution salary since gross is 14,500 or more';
            contributionHelp.className = 'text-info';
        }
    }
}

function showSalaryError(message) {
    const errorAlert = document.getElementById('salaryErrorAlert');
    const errorMessage = document.getElementById('salaryErrorMessage');
    
    if (errorAlert && errorMessage) {
        errorMessage.textContent = message;
        errorAlert.classList.remove('d-none');
    }
}

function hideSalaryAlerts() {
    const calculatingAlert = document.getElementById('salaryCalculatingAlert');
    const errorAlert = document.getElementById('salaryErrorAlert');
    
    if (calculatingAlert) calculatingAlert.classList.add('d-none');
    if (errorAlert) errorAlert.classList.add('d-none');
}

// Export functions that need to be accessed globally
window.initializeSalaryConversion = initializeSalaryConversion;
window.switchConversionType = switchConversionType;
window.calculateGrossToNet = calculateGrossToNet;
window.calculateNetToGross = calculateNetToGross;
window.hideSalaryAlerts = hideSalaryAlerts;
window.calculateContributionSalary = calculateContributionSalary;
window.calculateContributionSalaryFromGross = calculateContributionSalaryFromGross;