$(document).ready(function () {
    // Initialize Select2 for dropdowns
    $('#selectCustomer, #selectItem').select2({
        width: '100%',
        placeholder: 'Search...'
    });

    // load data on page ready
    loadCustomerIds();
    loadItems();

    let selectedCustomer = null;
    let cart = [];

    // load customer IDs only
    function loadCustomerIds() {
        $.ajax({
            url: baseURL + 'customer?action=ids',
            method: 'GET',
            success: function (response) {
                $('#selectCustomer').empty().append('<option value="">Select Customer</option>');
                response.customerIds.forEach(id => {
                    $('#selectCustomer').append(`<option value="${id}">${id}</option>`);
                });
            },
            error: function () {
                alert("Failed to load customer IDs!");
            }
        });
    }

    // when the customer is selected, fetch its details
    $('#selectCustomer').on('change', function () {
        const accountNumber = $(this).val();
        if (!accountNumber) {
            selectedCustomer = null;
            $('#customerDetails').hide();
            return;
        }

        $.ajax({
            url: baseURL + 'customer?accountNumber=' + accountNumber,
            method: 'GET',
            success: function (customer) {
                selectedCustomer = customer;
                $('#custAcc').text(customer.accountNumber);
                $('#custName').text(customer.name);
                $('#custAddress').text(customer.address);
                $('#custPhone').text(customer.telephone);
                $('#customerDetails').show();
            },
            error: function () {
                alert("Failed to fetch customer details!");
            }
        });
    });

    // load items into dropdown (with details upfront)
    function loadItems() {
        $.ajax({
            url: baseURL + 'item',
            method: 'GET',
            success: function (response) {
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
            },
            error: function () {
                alert("Failed to load items!");
            }
        });
    }

    // show selected item details
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

    // add item to cart
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

    // render cart table
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

    // remove item from cart
    $(document).on('click', '.remove-item', function () {
        const index = $(this).data('index');
        cart.splice(index, 1);
        renderCart();
    });

    // place order
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

});
