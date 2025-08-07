$(document).ready(function () {
    // Initialize Select2 dropdowns
    $('#reportCustomerFilter, #reportItemFilter').select2({
        theme: 'bootstrap-5',
        placeholder: 'Select...',
        allowClear: true,
        width: '100%'
    });

    // Load dropdown data
    loadReportFilters();

    // Event handlers
    $('#btnGenerateReport').on('click', generateDetailedReport);
    $('#btnGenerateTimeReport').on('click', generateTimeBasedReport);
    $('#btnClearFilters').on('click', clearReportFilters);

    // Load reports when tab is clicked
    $(document).on('click', '#reports-tab', function() {
        loadReportFilters();
    });

    function loadReportFilters() {
        // Load customers
        $.ajax({
            url: baseURL + 'customer?action=ids',
            method: 'GET',
            success: function(response) {
                const customerSelect = $('#reportCustomerFilter');
                customerSelect.empty().append('<option value="">All Customers</option>');
                
                if (response.customerIds) {
                    response.customerIds.forEach(id => {
                        customerSelect.append(`<option value="${id}">${id}</option>`);
                    });
                }
            }
        });

        // Load items
        $.ajax({
            url: baseURL + 'item',
            method: 'GET',
            success: function(response) {
                const itemSelect = $('#reportItemFilter');
                itemSelect.empty().append('<option value="">All Items</option>');
                
                if (response.items) {
                    response.items.forEach(item => {
                        itemSelect.append(`<option value="${item.itemCode}">${item.name} (${item.itemCode})</option>`);
                    });
                }
            }
        });
    }

    function generateDetailedReport() {
        const filters = getReportFilters();
        
        $.ajax({
            url: baseURL + 'reports?' + $.param(filters),
            method: 'GET',
            success: function(response) {
                displayDetailedReport(response);
                displaySummary(response.summary);
            },
            error: function() {
                NotificationService.error("Failed to generate report!");
            }
        });
    }

    function generateTimeBasedReport() {
        const filters = getReportFilters();
        filters.reportType = 'time-based';
        
        $.ajax({
            url: baseURL + 'reports?' + $.param(filters),
            method: 'GET',
            success: function(response) {
                displayTimeBasedReport(response);
            },
            error: function() {
                NotificationService.error("Failed to generate time-based report!");
            }
        });
    }

    function getReportFilters() {
        return {
            customerId: $('#reportCustomerFilter').val(),
            orderId: $('#reportOrderFilter').val(),
            itemCode: $('#reportItemFilter').val(),
            status: $('#reportStatusFilter').val(),
            paymentStatus: $('#reportPaymentFilter').val(),
            startDate: $('#reportStartDate').val(),
            endDate: $('#reportEndDate').val(),
            timeType: $('#reportTimeType').val()
        };
    }

    function displayDetailedReport(response) {
        const thead = $('#reportsTableHead');
        const tbody = $('#reportsTableBody');
        
        // Set table headers
        thead.html(`
            <tr>
                <th>Order Code</th>
                <th>Customer</th>
                <th>Order Date</th>
                <th>Items</th>
                <th>Total Amount</th>
                <th>Discount</th>
                <th>Status</th>
                <th>Payment</th>
            </tr>
        `);

        tbody.empty();

        if (!response.reports || response.reports.length === 0) {
            tbody.append('<tr><td colspan="8" class="text-center">No data found for the selected filters</td></tr>');
        } else {
            response.reports.forEach(report => {
                const statusMap = { A: "Active", I: "Inactive", D: "Deleted" };
                const statusClass = { A: "bg-success", I: "bg-secondary", D: "bg-danger" };
                const paymentStatusMap = { N: "Not Paid", P: "Pending", A: "Paid" };
                const paymentStatusClass = { N: "bg-danger", P: "bg-warning", A: "bg-success" };

                tbody.append(`
                    <tr>
                        <td>${report.orderCode}</td>
                        <td>${report.customerName}<br><small class="text-muted">${report.customerId}</small></td>
                        <td>${new Date(report.orderDate).toLocaleDateString()}</td>
                        <td>${report.itemCount} items</td>
                        <td>Rs. ${parseFloat(report.totalAmount).toFixed(2)}</td>
                        <td>Rs. ${parseFloat(report.totalDiscount).toFixed(2)}</td>
                        <td><span class="badge ${statusClass[report.status]}">${statusMap[report.status]}</span></td>
                        <td><span class="badge ${paymentStatusClass[report.paymentStatus]}">${paymentStatusMap[report.paymentStatus]}</span></td>
                    </tr>
                `);
            });
        }

        $('#reportsTableCard').show();
    }

    function displayTimeBasedReport(response) {
        const thead = $('#reportsTableHead');
        const tbody = $('#reportsTableBody');
        const timeType = $('#reportTimeType').val();

        // Set headers based on time type
        if (timeType === 'DAILY') {
            thead.html(`
                <tr>
                    <th>Date</th>
                    <th>Orders</th>
                    <th>Revenue</th>
                </tr>
            `);
        } else if (timeType === 'MONTHLY') {
            thead.html(`
                <tr>
                    <th>Year</th>
                    <th>Month</th>
                    <th>Orders</th>
                    <th>Revenue</th>
                </tr>
            `);
        } else {
            thead.html(`
                <tr>
                    <th>Year</th>
                    <th>Orders</th>
                    <th>Revenue</th>
                </tr>
            `);
        }

        tbody.empty();

        if (!response.timeReports || response.timeReports.length === 0) {
            tbody.append(`<tr><td colspan="${timeType === 'MONTHLY' ? 4 : 3}" class="text-center">No data found</td></tr>`);
        } else {
            response.timeReports.forEach(report => {
                if (timeType === 'DAILY') {
                    tbody.append(`
                        <tr>
                            <td>${report.report_date}</td>
                            <td>${report.order_count}</td>
                            <td>Rs. ${parseFloat(report.daily_revenue).toFixed(2)}</td>
                        </tr>
                    `);
                } else if (timeType === 'MONTHLY') {
                    const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                       "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
                    tbody.append(`
                        <tr>
                            <td>${report.report_year}</td>
                            <td>${monthNames[report.report_month - 1]}</td>
                            <td>${report.order_count}</td>
                            <td>Rs. ${parseFloat(report.monthly_revenue).toFixed(2)}</td>
                        </tr>
                    `);
                } else {
                    tbody.append(`
                        <tr>
                            <td>${report.report_year}</td>
                            <td>${report.order_count}</td>
                            <td>Rs. ${parseFloat(report.annual_revenue).toFixed(2)}</td>
                        </tr>
                    `);
                }
            });
        }

        $('#reportsTableCard').show();
        $('#reportSummaryCards').hide();
    }

    function displaySummary(summary) {
        if (summary) {
            $('#summaryTotalOrders').text(summary.totalOrders);
            $('#summaryTotalRevenue').text('Rs. ' + parseFloat(summary.totalRevenue).toFixed(2));
            $('#summaryTotalDiscounts').text('Rs. ' + parseFloat(summary.totalDiscounts).toFixed(2));
            $('#summaryAvgOrderValue').text('Rs. ' + parseFloat(summary.avgOrderValue).toFixed(2));
            $('#reportSummaryCards').show();
        }
    }

    function clearReportFilters() {
        $('#reportCustomerFilter, #reportItemFilter, #reportStatusFilter, #reportPaymentFilter').val('').trigger('change');
        $('#reportOrderFilter, #reportStartDate, #reportEndDate').val('');
        $('#reportTimeType').val('DAILY');
        $('#reportsTableCard, #reportSummaryCards').hide();
    }
});