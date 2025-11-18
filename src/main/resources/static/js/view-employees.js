// Delete modal functionality
function showDeleteModal(employeeId, employeeName) {
    console.log('Deleting employee:', employeeId, employeeName);
    const form = document.getElementById('deleteForm');
    form.action = '/employees/delete/' + employeeId;
    document.getElementById('employeeName').textContent = employeeName || 'this employee';
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    deleteModal.show();
}

// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const searchText = this.value.toLowerCase();
            const rows = document.querySelectorAll('#employeesTable tbody tr');
            let visibleCount = 0;
            
            rows.forEach(row => {
                if (row.id === 'emptyStateRow') {
                    return;
                }
                
                const text = row.textContent.toLowerCase();
                if (text.includes(searchText)) {
                    row.style.display = '';
                    visibleCount++;
                } else {
                    row.style.display = 'none';
                }
            });
            
            // Show empty state if no results
            const emptyStateRow = document.getElementById('emptyStateRow');
            if (emptyStateRow) {
                if (visibleCount === 0 && searchText !== '') {
                    emptyStateRow.style.display = '';
                    emptyStateRow.querySelector('.empty-state').innerHTML = `
                        <i class="fas fa-search"></i>
                        <h5>No Employees Found</h5>
                        <p class="mb-3">No employees match your search criteria.</p>
                        <button class="btn btn-primary-custom" onclick="clearSearch()">
                            <i class="fas fa-times me-2"></i> Clear Search
                        </button>
                    `;
                } else if (searchText === '') {
                    const employeeRows = document.querySelectorAll('#employeesTable tbody tr:not(#emptyStateRow)');
                    const hasEmployees = employeeRows.length > 0 && employeeRows[0].style.display !== 'none';
                    emptyStateRow.style.display = hasEmployees ? 'none' : '';
                } else {
                    emptyStateRow.style.display = 'none';
                }
            }
        });
    }
});

function clearSearch() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
        searchInput.dispatchEvent(new Event('keyup'));
    }
}

// Auto-dismiss alerts after 5 seconds
setTimeout(function() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        const bsAlert = new bootstrap.Alert(alert);
        bsAlert.close();
    });
}, 5000);