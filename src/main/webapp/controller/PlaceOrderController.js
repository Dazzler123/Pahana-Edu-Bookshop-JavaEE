$(document).ready(function () {
    // Initialize Select2 for dropdowns
    $('#selectCustomer, #selectItem').select2({
        width: '100%',
        placeholder: 'Search...'
    });

    let selectedCustomer = null;
    let cart = [];

    // Load customers into dropdown
    function loadCustomers() {
        $.ajax({
            url: baseURL + 'customer',
            method: 'GET',
            success: function (response) {
                if (response.customers) {
                    $('#selectCustomer').empty().append('<option value="">Select Customer</option>');
                    response.customers.forEach(c => {
                        if (c.status === 'A') { // Only active customers
                            $('#selectCustomer').append(`<option value="${c.accountNumber}" 
                                data-name="${c.name}" 
                                data-address="${c.address}" 
                                data-telephone="${c.telephone}">
                                ${c.accountNumber} - ${c.name}
                            </option>`);
                        }
                    });
                }
            },
            error: function (err) {
                alert("Failed to load customers!");
            }
        });
    }

    // Load items into dropdown
    function loadItems() {
        $.ajax({
            url: baseURL + 'item',
            method: 'GET',
            success: function (response) {
                if (response.items) {
                    $('#selectItem').empty().append('<option value="">Select Item</option>');
                    response.items.forEach(i => {
                        if (i.status === 'A') { // Only active items
                            $('#selectItem').append(`<option value="${i.itemCode}" 
                                data-name="${i.name}" 
                                data-price="${i.unitPrice}" 
                                data-stock="${i.qtyOnHand}">
                                ${i.itemCode} - ${i.name}
                            </option>`);
                        }
                    });
                }
            },
            error: function () {
                alert("Failed to load items!");
            }
        });
    }

    // Show selected customer details
    $('#selectCustomer').on('change', function () {
        const option = $(this).find(':selected');
        if (option.val()) {
            selectedCustomer = {
                accountNumber: option.val(),
                name: option.data('name'),
                address: option.data('address'),
                telephone: option.data('telephone')
            };
            $('#custAcc').text(selectedCustomer.accountNumber);
            $('#custName').text(selectedCustomer.name);
            $('#custAddress').text(selectedCustomer.address);
            $('#custPhone').text(selectedCustomer.telephone);
            $('#customerDetails').show();
        } else {
            selectedCustomer = null;
            $('#customerDetails').hide();
        }
    });

    // Show selected item details
    $('#selectItem').on('change', function () {
        const option = $(this).find(':selected');
        if (option.val()) {
            $('#itemCode').text(option.val());
            $('#itemName').text(option.data('name'));
            $('#itemPrice').text(option.data('price'));
            $('#itemStock').text(option.data('stock'));
            $('#itemDetails').show();
        } else {
            $('#itemDetails').hide();
        }
    });

    // Add item to cart
    $('#btnAddItem').on('click', function () {
        const option = $('#selectItem').find(':selected');
        const qty = parseInt($('#itemQty').val());
        if (!option.val()) return alert("Please select an item!");
        if (!qty || qty <= 0) return alert("Please enter a valid quantity!");

        const stock = parseInt(option.data('stock'));
        if (qty > stock) return alert("Not enough stock!");

        const existing = cart.find(item => item.code === option.val());
        if (existing) {
            existing.qty += qty;
        } else {
            cart.push({
                code: option.val(),
                name: option.data('name'),
                price: parseFloat(option.data('price')),
                qty: qty
            });
        }
        renderCart();
        $('#itemQty').val('');
    });

    // Render cart table
    function renderCart() {
        $('#orderTableBody').empty();
        let total = 0;
        cart.forEach((item, index) => {
            const itemTotal = item.price * item.qty;
            total += itemTotal;
            $('#orderTableBody').append(`
                <tr>
                    <td>${item.code}</td>
                    <td>${item.name}</td>
                    <td>${item.qty}</td>
                    <td>${item.price.toFixed(2)}</td>
                    <td>${itemTotal.toFixed(2)}</td>
                    <td><button class="btn btn-danger btn-sm remove-item" data-index="${index}">Remove</button></td>
                </tr>
            `);
        });
        $('#grandTotal').text(total.toFixed(2));
    }

    // Remove item from cart
    $(document).on('click', '.remove-item', function () {
        const index = $(this).data('index');
        cart.splice(index, 1);
        renderCart();
    });

    // Place order
    $('#btnPlaceOrder').on('click', function () {
        if (!selectedCustomer) return alert("Select a customer!");
        if (cart.length === 0) return alert("Cart is empty!");

        const order = {
            customerAccount: selectedCustomer.accountNumber,
            items: cart
        };

        $.ajax({
            url: baseURL + 'order',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(order),
            success: function (response) {
                alert(response.message || "Order placed successfully!");
                cart = [];
                renderCart();
                $('#selectCustomer').val('').trigger('change');
                $('#selectItem').val('').trigger('change');
                $('#customerDetails, #itemDetails').hide();
            },
            error: function () {
                alert("Failed to place order!");
            }
        });
    });

    // Load data on page ready
    loadCustomers();
    loadItems();
});
