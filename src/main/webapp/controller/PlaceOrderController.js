$(document).ready(function () {
    // Initialize Select2 for dropdowns
    $('#selectCustomer, #selectItem').select2({
        theme: 'bootstrap-5',
        placeholder: 'Search...',
        allowClear: true,
        width: '100%'
    });

    // load data on page ready
    loadCustomerIds();
    loadItems();
    // hide forms
    disableOrderInputs(true);

    let selectedCustomer = null;
    let cart = [];
    let editIndex = null;

    // this is to disable specific forms if data is incomplete
    function disableOrderInputs(state) {
        $('#selectItem').prop('disabled', state);
        $('#itemQty').prop('disabled', state);
        $('#itemDiscount').prop('disabled', state);
        $('#btnAddItem').prop('disabled', state);
        $('#orderTable').toggleClass('table-disabled', state);
    }

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
            disableOrderInputs(true);
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
                disableOrderInputs(false); // Enable item selection now
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
        const discount = parseFloat($('#itemDiscount').val()) || 0;

        if (!option.val()) return alert("Please select an item!");
        if (!qty || qty <= 0) return alert("Please enter a valid quantity!");
        if (discount < 0 || discount > 100) return alert("Discount must be between 0 and 100!");

        const stock = parseInt(option.data('stock'));
        if (qty > stock) return alert("Not enough stock!");

        if (editIndex !== null) {
            // validate new qty against stock
            const availableStock = stock + cart[editIndex].qty; // add back old qty
            if (qty > availableStock) return alert("Not enough stock!");

            // update item
            cart[editIndex].qty = qty;
            cart[editIndex].discount = discount;

            // reset Edit Mode
            editIndex = null;
            $('#btnAddItem').text('Add Item').removeClass('btn-primary').addClass('btn-success');
            $('#btnResetItem').hide();
            $('#selectItem').prop('disabled', false);
        } else {
            // add new item
            const existing = cart.find(item => item.code === option.val());
            if (existing) {
                if (existing.qty + qty > stock) {
                    return alert(`Cannot add more than stock (${stock})`);
                }
                existing.qty += qty;
                existing.discount = discount;
            } else {
                cart.push({
                    code: option.val(),
                    name: option.data('name'),
                    price: parseFloat(option.data('price')),
                    qty: qty,
                    discount: discount
                });
            }
        }

        renderCart();

        // reset form after add/update
        $('#itemQty').val('');
        $('#itemDiscount').val('');
        $('#selectItem').val('').trigger('change');
        $('#itemDetails').hide();
    });


    // this is to reset the item form
    $('#btnResetItem').on('click', function () {
        editIndex = null;
        $('#selectItem').val('').trigger('change');
        $('#itemQty').val('');
        $('#itemDiscount').val('');
        $('#btnAddItem').text('Add Item').removeClass('btn-primary').addClass('btn-success');
        $('#btnResetItem').hide();
        $('#selectItem').prop('disabled', false);
    });


    // render cart table
    function renderCart() {
        $('#orderTableBody').empty();
        let total = 0;

        cart.forEach((item, index) => {
            const discountedPrice = item.price - (item.price * (item.discount / 100));
            const itemTotal = discountedPrice * item.qty;
            total += itemTotal;

            $('#orderTableBody').append(`
            <tr>
                <td>${item.code}</td>
                <td>${item.name}</td>
                <td>${item.qty}</td>
                <td>${item.price.toFixed(2)}</td>
                <td>${item.discount.toFixed(2)}%</td>
                <td>${itemTotal.toFixed(2)}</td>
                <td>
                     <button class="btn btn-warning btn-sm edit-item me-1" data-index="${index}">Edit</button>
                     <button class="btn btn-danger btn-sm remove-item" data-index="${index}">Remove</button>
                </td>
            </tr>
        `);
        });

        $('#grandTotal').text(total.toFixed(2));
        togglePlaceOrderButton(); // enable place order button
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

        const orderPayload = {
            customerAccount: selectedCustomer.accountNumber,
            items: cart.map(item => ({
                itemCode: item.code,
                qty: item.qty,
                unitPrice: item.price,
                discount: item.discount,
                total: item.total
            }))
        };

        $.ajax({
            url: baseURL + 'place-order',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(orderPayload),
            success: function (response) {
                alert(response.message || "Order placed successfully!");
                resetOrderForm();
            },
            error: function () {
                alert("Failed to place order!");
            }
        });
    });


    // reset place order form
    $('#btnResetOrder').on('click', function () {
        if (confirm("Are you sure you want to reset this order?")) {
            cart = [];
            selectedCustomer = null;
            $('#selectCustomer').val('').trigger('change');

            $('#btnResetItem').hide();
            $('#selectItem').prop('disabled', false); // re-enable dropdown
            $('#selectItem').val('').trigger('change');

            $('#btnAddItem').text('Add Item').removeClass('btn-primary').addClass('btn-success');
            editIndex = null;
            $('#customerDetails, #itemDetails').hide();
            $('#itemQty').val('');
            $('#itemDiscount').val('');
            renderCart();
            disableOrderInputs(true);
            $('#btnPlaceOrder').prop('disabled', true);
        }
    });

    // this is to keep the place order button disabled if the cart is empty
    function togglePlaceOrderButton() {
        $('#btnPlaceOrder').prop('disabled', cart.length === 0);
    }

    // this is to update form when a item in the cart is edited
    $(document).on('click', '.edit-item', function () {
        editIndex = $(this).data('index');
        const item = cart[editIndex];

        // populate fields
        $('#selectItem').val(item.code).trigger('change');
        $('#itemQty').val(item.qty);
        $('#itemDiscount').val(item.discount);

        // switch to Edit Mode
        $('#btnAddItem').text('Update Item').removeClass('btn-success').addClass('btn-primary');
        $('#btnResetItem').show();
        $('#selectItem').prop('disabled', true);
    });


    togglePlaceOrderButton(); // keep place order main button disabled at the initial stage

});
