$(document).ready(function () {

    // Load dashboard data when dashboard tab is clicked
    $(document).on('click', '#manage-dashboard-tab', function() {
        loadDashboardData();
    });

    // Load dashboard data on page load if dashboard is active
    if ($('#dashboard-content').is(':visible')) {
        loadDashboardData();
    }

    function loadDashboardData() {
        loadKeyMetrics();
        loadMostVisitedCustomers();
        loadTopSellingItems();
    }

    // Load key metrics
    function loadKeyMetrics() {
        // Load total customers
        $.ajax({
            url: baseURL + 'customer',
            method: 'GET',
            success: function(response) {
                const activeCustomers = response.customers.filter(c => c.status === 'A').length;
                $('#totalCustomers').text(activeCustomers);
            }
        });

        // Load dashboard stats
        $.ajax({
            url: baseURL + 'dashboard-stats',
            method: 'GET',
            success: function(response) {
                $('#totalOrders').text(response.totalOrders || 0);
                $('#pendingOrders').text(response.pendingOrders || 0);
                $('#totalRevenue').text('Rs. ' + (response.totalRevenue || 0).toFixed(2));
            },
            error: function() {
                console.log('Dashboard stats endpoint not available');
            }
        });
    }

    // Load most visited customers
    function loadMostVisitedCustomers() {
        $.ajax({
            url: baseURL + 'dashboard-analytics?type=most-visited-customers',
            method: 'GET',
            success: function(response) {
                const tbody = $('#recentOrdersTable');
                tbody.empty();

                if (!response.customers || response.customers.length === 0) {
                    tbody.append('<tr><td colspan="4" class="text-center">No customer data available</td></tr>');
                    return;
                }

                response.customers.forEach(customer => {
                    const rankBadges = {
                        1: '<span class="badge bg-warning text-dark">🥇 1st</span>',
                        2: '<span class="badge bg-secondary">🥈 2nd</span>',
                        3: '<span class="badge bg-warning text-dark">🥉 3rd</span>',
                        4: '<span class="badge bg-primary">4th</span>',
                        5: '<span class="badge bg-info">5th</span>'
                    };

                    tbody.append(`
                        <tr>
                            <td>${rankBadges[customer.rank] || customer.rank}</td>
                            <td>${customer.name}</td>
                            <td>${customer.orderCount} orders</td>
                            <td>Rs. ${parseFloat(customer.totalSpent).toFixed(2)}</td>
                        </tr>
                    `);
                });
            },
            error: function() {
                $('#recentOrdersTable').html('<tr><td colspan="4" class="text-center text-danger">Failed to load customer analytics data</td></tr>');
            }
        });
    }

    // Load top selling items
    function loadTopSellingItems() {
        $.ajax({
            url: baseURL + 'dashboard-analytics?type=top-selling-items',
            method: 'GET',
            success: function(response) {
                const tbody = $('#topItemsTable');
                tbody.empty();

                if (!response.items || response.items.length === 0) {
                    tbody.append('<tr><td colspan="3" class="text-center">No item data available</td></tr>');
                    return;
                }

                response.items.forEach(item => {
                    const rankBadges = {
                        1: '<span class="badge bg-warning text-dark">🥇 1st</span>',
                        2: '<span class="badge bg-secondary">🥈 2nd</span>',
                        3: '<span class="badge bg-warning text-dark">🥉 3rd</span>',
                        4: '<span class="badge bg-primary">4th</span>',
                        5: '<span class="badge bg-info">5th</span>'
                    };

                    tbody.append(`
                        <tr>
                            <td>
                                ${rankBadges[item.rank] || item.rank}
                                <br><small class="text-muted">${item.name}</small>
                            </td>
                            <td>${item.totalSold} units</td>
                            <td>Rs. ${parseFloat(item.totalRevenue).toFixed(2)}</td>
                        </tr>
                    `);
                });
            },
            error: function() {
                $('#topItemsTable').html('<tr><td colspan="3" class="text-center text-danger">Failed to load item analytics data</td></tr>');
            }
        });
    }
});