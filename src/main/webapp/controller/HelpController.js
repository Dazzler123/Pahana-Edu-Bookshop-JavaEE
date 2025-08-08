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
        
        // Fix tab navigation - ensure proper active states
        initializeHelpTabs();
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
            userEmail: $('#userEmail').val(),
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent
        };

        // Validate required fields
        if (!formData.issueType || !formData.subject || !formData.description) {
            NotificationService.error('Please fill in all required fields.');
            return;
        }

        NotificationService.loading('Submitting support request...');
        
        $.ajax({
            url: baseURL + 'support-request',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                NotificationService.closeLoading();
                
                if (response.success) {
                    const message = `Support request submitted successfully!\n\nTicket ID: ${response.ticketId}\nExpected response time: ${response.responseTime}`;
                    NotificationService.success(message);
                    $('#supportForm')[0].reset();
                    
                    // Show ticket ID in a more prominent way
                    showTicketConfirmation(response.ticketId, response.responseTime);
                } else {
                    NotificationService.error(response.message || 'Failed to submit support request.');
                }
            },
            error: function(xhr) {
                NotificationService.closeLoading();
                const errorMsg = xhr.responseJSON?.message || 'Failed to submit support request. Please try again.';
                NotificationService.error(errorMsg);
            }
        });
    }

    function showTicketConfirmation(ticketId, responseTime) {
        const confirmationHtml = `
            <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                <h5><i class="bi bi-check-circle me-2"></i>Request Submitted Successfully!</h5>
                <p><strong>Ticket ID:</strong> <code>${ticketId}</code></p>
                <p><strong>Expected Response Time:</strong> ${responseTime}</p>
                <small>Please save your ticket ID for future reference.</small>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        
        $('#supportForm').after(confirmationHtml);
        
        // Auto-remove after 10 seconds
        setTimeout(() => {
            $('.alert-success').fadeOut();
        }, 10000);
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

    function initializeHelpTabs() {
        // Handle tab clicks manually to ensure proper active states
        $('#helpTabs .nav-link').on('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all tabs
            $('#helpTabs .nav-link').removeClass('active');
            
            // Add active class to clicked tab
            $(this).addClass('active');
            
            // Hide all tab panes
            $('.tab-pane').removeClass('show active');
            
            // Show the target pane
            const targetPane = $(this).attr('data-bs-target');
            $(targetPane).addClass('show active');
        });
        
        // Set initial active state - make sure one tab is always active
        if ($('#helpTabs .nav-link.active').length === 0) {
            $('#user-guide-tab').addClass('active');
            $('#user-guide').addClass('show active');
        }
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
        },
        
        // Function to manually activate a specific tab
        activateTab: function(tabId) {
            $('#helpTabs .nav-link').removeClass('active');
            $('.tab-pane').removeClass('show active');
            
            $(`#${tabId}-tab`).addClass('active');
            $(`#${tabId}`).addClass('show active');
        }
    };

    // Auto-activate system-info tab if it's being shown
    if ($('#system-info').hasClass('show') || $('#system-info').hasClass('active')) {
        $('#system-info-tab').addClass('active');
        $('#helpTabs .nav-link').not('#system-info-tab').removeClass('active');
    }
});