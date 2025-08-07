$(document).ready(function () {
    
    // Initialize help section
    initializeHelpSection();
    
    // Support form submission
    $('#supportForm').on('submit', function(e) {
        e.preventDefault();
        submitSupportRequest();
    });

    // Keyboard shortcuts
    $(document).on('keydown', function(e) {
        if (e.altKey) {
            switch(e.key.toLowerCase()) {
                case 'd': $('#manage-dashboard-tab').click(); break;
                case 'c': $('#manage-customers-tab').click(); break;
                case 'i': $('#manage-items-tab').click(); break;
                case 'o': $('#manage-orders-tab').click(); break;
                case 'p': $('#place-order-tab').click(); break;
                case 'r': $('#reports-tab').click(); break;
                case 'h': $('#help-section-tab').click(); break;
            }
        }
        
        if (e.ctrlKey && e.key.toLowerCase() === 's') {
            e.preventDefault();
            // Find active form and submit
            const activeForm = $('.tab-pane.active form:visible, .show form:visible').first();
            if (activeForm.length) {
                activeForm.submit();
            }
        }
    });

    function initializeHelpSection() {
        // Set system last updated date
        $('#systemLastUpdated').text(new Date().toLocaleDateString());
        
        // Check database status
        checkDatabaseStatus();
        
        // Add search functionality to help content
        addHelpSearch();
    }

    function checkDatabaseStatus() {
        $.ajax({
            url: baseURL + 'dashboard-stats',
            method: 'GET',
            success: function() {
                $('#dbStatus').removeClass('bg-danger').addClass('bg-success').text('Connected');
            },
            error: function() {
                $('#dbStatus').removeClass('bg-success').addClass('bg-danger').text('Disconnected');
            }
        });
    }

    function submitSupportRequest() {
        const formData = {
            issueType: $('#issueType').val(),
            priority: $('#priority').val(),
            subject: $('#supportSubject').val(),
            description: $('#supportDescription').val(),
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent
        };

        // Simulate support request submission
        NotificationService.loading('Submitting support request...');
        
        setTimeout(() => {
            NotificationService.closeLoading();
            NotificationService.success('Support request submitted successfully! You will receive a response within ' + 
                                      getResponseTime(formData.priority) + '.');
            $('#supportForm')[0].reset();
        }, 2000);
    }

    function getResponseTime(priority) {
        switch(priority) {
            case 'urgent': return '2-4 hours';
            case 'high': return '2-4 hours';
            case 'medium': return '24 hours';
            case 'low': return '48-72 hours';
            default: return '24-48 hours';
        }
    }

    function addHelpSearch() {
        // Add search box to help section
        const searchHtml = `
            <div class="mb-3">
                <div class="input-group">
                    <input type="text" id="helpSearch" class="form-control" placeholder="Search help topics...">
                    <button class="btn btn-outline-secondary" type="button" id="helpSearchBtn">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </div>
        `;
        
        $('#user-guide').prepend(searchHtml);
        
        // Search functionality
        $('#helpSearch, #helpSearchBtn').on('input click', function() {
            const searchTerm = $('#helpSearch').val().toLowerCase();
            
            $('.accordion-item').each(function() {
                const text = $(this).text().toLowerCase();
                if (searchTerm === '' || text.includes(searchTerm)) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        });
    }

    // Export help functions for global access
    window.HelpController = {
        openHelpTopic: function(topic) {
            $('#help-section-tab').click();
            setTimeout(() => {
                $(`#${topic}Guide .accordion-button`).click();
            }, 100);
        },
        
        showKeyboardShortcuts: function() {
            $('#help-section-tab').click();
            setTimeout(() => {
                $('#shortcuts-tab').click();
            }, 100);
        }
    };
});