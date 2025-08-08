$(document).ready(function () {
    // Initialize Select2 for customer dropdown
    $('#selectOrderCustomer').select2({
        theme: 'bootstrap-5',
        placeholder: 'Search...',
        allowClear: true,
        width: '100%'
    });

    loadCustomerIdsForOrders();
    attachOrderSearchFilters();
    
    let selectedOrderForEdit = null;

    // Reset filters button
    $("#btn-reset-order-filters").on("click", function () {
        $("#searchOrderCode, #searchOrderDate, #searchTotalAmount").val("");
        $("#searchOrderStatus, #searchPaymentStatus").val("");
        filterOrders();
        $("#searchOrderCode").focus();
    });

    // Load customer IDs for dropdown with full customer details
    function loadCustomerIdsForOrders() {
        $.ajax({
            url: baseURL + 'customer',
            method: 'GET',
            success: function (response) {
                $('#selectOrderCustomer').empty().append('<option value="">Select Customer...</option>');
                response.customers.forEach(customer => {
                    if (customer.status === 'A') { // only active customers
                        $('#selectOrderCustomer').append(`<option value="${customer.accountNumber}">
                            ${customer.accountNumber} - ${customer.name}
                        </option>`);
                    }
                });
            },
            error: function () {
                NotificationService.error("Failed to load customers!");
            }
        });
    }

    // Load orders when customer is selected
    $('#selectOrderCustomer').on('change', function () {
        const customerId = $(this).val();
        if (!customerId) {
            $('#manageOrderTableBody').empty();
            $('#orderEditForm').hide();
            return;
        }
        loadCustomerOrders(customerId);
    });

    // Load orders for selected customer
    function loadCustomerOrders(customerId) {
        $.ajax({
            url: baseURL + 'manage-orders?customerId=' + customerId,
            method: 'GET',
            success: function (response) {
                console.log('Orders response:', response); // Debug log
                const tbody = $('#manageOrderTableBody');
                tbody.empty();
                
                if (!response.orders || response.orders.length === 0) {
                    tbody.append('<tr><td colspan="7" class="text-center">No orders found for this customer</td></tr>');
                    return;
                }
                
                response.orders.forEach(order => {
                    const statusMap = { A: "Active", I: "Inactive", D: "Deleted" };
                    const statusClass = { A: "bg-success", I: "bg-secondary", D: "bg-danger" };
                    const paymentStatusMap = { 
                        N: "Not Paid",
                        P: "Pending", 
                        A: "Paid"
                    };
                    const paymentStatusClass = {
                        N: "bg-danger",
                        P: "bg-warning",
                        A: "bg-success"
                    };
                    
                    const row = $(`
                        <tr data-order='${JSON.stringify(order)}'>
                            <td>${order.orderCode}</td>
                            <td>${new Date(order.orderDate).toLocaleDateString()}</td>
                            <td>Rs. ${parseFloat(order.totalAmount).toFixed(2)}</td>
                            <td>Rs. ${parseFloat(order.totalDiscount).toFixed(2)}</td>
                            <td><span class="badge ${statusClass[order.status] || 'bg-dark'}">${statusMap[order.status] || 'Unknown'}</span></td>
                            <td><span class="badge ${paymentStatusClass[order.paymentStatus] || 'bg-dark'}">${paymentStatusMap[order.paymentStatus] || order.paymentStatus}</span></td>
                            <td>${order.paymentType || 'N/A'}</td>
                            <td>
                                <button class="btn btn-sm btn-success activate-btn" ${order.status !== 'I' ? 'disabled' : ''}>Activate</button>
                                <button class="btn btn-sm btn-warning inactivate-btn" ${order.status !== 'A' ? 'disabled' : ''}>Inactivate</button>
                                <button class="btn btn-sm btn-primary edit-btn" ${order.status === 'D' ? 'disabled' : ''}>Edit</button>
                                <button class="btn btn-sm btn-danger delete-btn" ${order.status === 'D' ? 'disabled' : ''}>Delete</button>
                            </td>
                        </tr>
                    `);

                    // Edit button click
                    row.find('.edit-btn').on('click', function() {
                        selectedOrderForEdit = order;
                        populateEditForm(order);
                        $('#orderEditForm').show();
                    });

                    // Status update buttons
                    row.find('.activate-btn').on('click', () => updateOrderStatus(order.orderCode, 'A'));
                    row.find('.inactivate-btn').on('click', () => {
                        NotificationService.confirm("Are you sure to inactivate this order?")
                            .then((result) => {
                                if (result.isConfirmed) {
                                    updateOrderStatus(order.orderCode, 'I');
                                }
                            });
                    });
                    row.find('.delete-btn').on('click', () => {
                        NotificationService.confirm("Are you sure to delete this order?")
                            .then((result) => {
                                if (result.isConfirmed) {
                                    updateOrderStatus(order.orderCode, 'D');
                                }
                            });
                    });

                    tbody.append(row);
                });
                
                filterOrders(); // Apply current filters after loading
            },
            error: function (xhr, status, error) {
                console.error('Failed to load orders:', error); // Debug log
                NotificationService.error("Failed to load orders!");
            }
        });
    }

    // Attach search filter event listeners
    function attachOrderSearchFilters() {
        $("#searchOrderCode, #searchOrderDate, #searchTotalAmount, #searchOrderStatus, #searchPaymentStatus").on("input change", filterOrders);
    }

    // Filter orders function
    function filterOrders() {
        const codeFilter = $("#searchOrderCode").val().toLowerCase();
        const dateFilter = $("#searchOrderDate").val();
        const amountFilter = $("#searchTotalAmount").val().toLowerCase();
        const statusFilter = $("#searchOrderStatus").val();
        const paymentStatusFilter = $("#searchPaymentStatus").val();

        $("#manageOrderTableBody tr").each(function () {
            const cells = $(this).children();
            if (cells.length < 8) return; // skip malformed rows

            const orderDate = cells.eq(1).text();
            const dateMatch = !dateFilter || (orderDate && new Date(orderDate).toISOString().slice(0, 10) === dateFilter);
            
            // Get status from badge text
            const statusBadge = cells.eq(4).find('.badge').text();
            const statusMatch = !statusFilter || 
                (statusFilter === 'A' && statusBadge === 'Active') ||
                (statusFilter === 'I' && statusBadge === 'Inactive') ||
                (statusFilter === 'D' && statusBadge === 'Deleted');
            
            // Get payment status from badge text
            const paymentBadge = cells.eq(5).find('.badge').text();
            const paymentMatch = !paymentStatusFilter ||
                (paymentStatusFilter === 'N' && paymentBadge === 'Not Paid') ||
                (paymentStatusFilter === 'P' && paymentBadge === 'Pending') ||
                (paymentStatusFilter === 'A' && paymentBadge === 'Paid');

            const match =
                cells.eq(0).text().toLowerCase().includes(codeFilter) &&
                dateMatch &&
                cells.eq(2).text().toLowerCase().includes(amountFilter) &&
                statusMatch &&
                paymentMatch;
                
            $(this).toggle(match);
        });
    }

    // Populate edit form with order data
    function populateEditForm(order) {
        $('#editOrderCode').val(order.orderCode);
        $('#editOrderDate').val(new Date(order.orderDate).toISOString().slice(0, 16));
        $('#editTotalAmount').val(order.totalAmount);
        $('#editTotalDiscount').val(order.totalDiscount);
        $('#editPaymentStatus').val(order.paymentStatus);
        $('#editPaymentType').val(order.paymentType || 'Cash');
    }

    // Update order form submission
    $('#updateOrderForm').on('submit', function(e) {
        e.preventDefault();
        
        if (!selectedOrderForEdit) return;

        const updatedOrder = {
            orderCode: $('#editOrderCode').val(),
            orderDate: $('#editOrderDate').val(),
            totalAmount: parseFloat($('#editTotalAmount').val()),
            totalDiscount: parseFloat($('#editTotalDiscount').val()),
            status: selectedOrderForEdit.status,
            paymentStatus: $('#editPaymentStatus').val(),
            paymentType: $('#editPaymentType').val()
        };

        $.ajax({
            url: baseURL + 'manage-orders',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(updatedOrder),
            success: function (response) {
                NotificationService.success(response.message);
                loadCustomerOrders($('#selectOrderCustomer').val());
                $('#orderEditForm').hide();
                selectedOrderForEdit = null;
            },
            error: function () {
                NotificationService.error("Failed to update order!");
            }
        });
    });

    // Update order status function
    function updateOrderStatus(orderCode, newStatus) {
        $.ajax({
            url: baseURL + 'manage-orders',
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ 
                orderCode: orderCode, 
                status: newStatus 
            }),
            success: function (response) {
                NotificationService.success(response.message);
                loadCustomerOrders($('#selectOrderCustomer').val());
            },
            error: function (xhr, status, error) {
                console.error('Failed to update order status:', error);
                NotificationService.error("Failed to update order status!");
            }
        });
    }

    // Cancel edit
    $('#btnCancelOrderEdit').on('click', function() {
        $('#orderEditForm').hide();
        selectedOrderForEdit = null;
    });
});
