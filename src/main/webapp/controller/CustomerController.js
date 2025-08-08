$(document).ready(function () {
    loadAllCustomers();
    attachCustomerSearchFilters();

    $("#btn-customer-reset-filters").on("click", function () {
        $("#searchCustomerAccountNumber, #searchCustomerName, #searchCustomerAddress, #searchCustomerTelephone").val("");
        filterCustomers();
        $("#searchCustomerAccountNumber").focus();
    });

    $("#btn-reset-customer-form").on("click", function () {
        resetCustomerFormUI();
    });
});

let isCustomerUpdateMode = false;
let existingCustomerStatus = null;


// create customer
$("#addCustomerForm").on("submit", function (e) {
    e.preventDefault();
    const form = $(this)[0];
    const formData = new FormData(form);
    if (isCustomerUpdateMode) formData.set("status", existingCustomerStatus);
    const formParams = new URLSearchParams(formData).toString();

    fetch(baseURL + "customer", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formParams
    })
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            NotificationService.success(data.message);
            form.reset();
            resetCustomerFormUI();
            loadAllCustomers();
        })
        .catch(error => NotificationService.error("Error: " + error.message));
});

function resetCustomerFormUI() {
    isCustomerUpdateMode = false;
    existingCustomerStatus = null;
    $("#add-new-customer-form-header").text("Add New Customer");
    const submitBtn = $("#btn-create-customer");
    submitBtn.text("Add Customer").removeClass("btn-warning").addClass("btn-success");
    $("#customer-account-number").prop("readOnly", true); // Make it read-only
    generateAccountNumber(); // Generate new account number
}

function loadAllCustomers() {
    fetch(baseURL + "customer")
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            const tbody = $("#customerTableBody");
            tbody.empty();
            data.customers.forEach(c => {
                const statusMap = { A: "Active", I: "Inactive", D: "Deleted" };
                const statusClass = { A: "bg-success", I: "bg-secondary", D: "bg-danger" };
                const row = $(`
                    <tr>
                        <td>${c.accountNumber}</td>
                        <td>${c.name}</td>
                        <td>${c.address}</td>
                        <td>${c.telephone}</td>
                        <td><span class="badge ${statusClass[c.status] || 'bg-dark'}">${statusMap[c.status] || 'Unknown'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-success activate-btn" ${c.status !== 'I' ? 'disabled' : ''}>Activate</button>
                            <button class="btn btn-sm btn-warning inactivate-btn" ${c.status !== 'A' ? 'disabled' : ''}>Inactivate</button>
                            <button class="btn btn-sm btn-primary edit-btn" ${c.status === 'D' ? 'disabled' : ''}>Edit</button>
                            <button class="btn btn-sm btn-danger delete-btn" ${c.status === 'D' ? 'disabled' : ''}>Delete</button>
                        </td>
                    </tr>`);

                row.find(".edit-btn").on("click", () => {
                    $("#add-new-customer-form-header").text("Update Customer");
                    $("#customer-account-number").val(c.accountNumber).prop("readOnly", true);
                    $("#customer-name").val(c.name);
                    $("#customer-address").val(c.address);
                    $("#customer-telephone").val(c.telephone);
                    $("#customer-status").val(c.status);
                    isCustomerUpdateMode = true;
                    existingCustomerStatus = c.status;
                    $("#btn-create-customer").text("Update Customer").removeClass("btn-success").addClass("btn-warning");
                });

                row.find(".activate-btn").on("click", () => updateCustomerStatus(c.accountNumber, 'A'));
                row.find(".inactivate-btn").on("click", () => {
                    NotificationService.confirm("Are you sure to inactivate this customer?")
                        .then((result) => {
                            if (result.isConfirmed) {
                                updateCustomerStatus(c.accountNumber, 'I');
                            }
                        });
                });
                row.find(".delete-btn").on("click", () => {
                    NotificationService.confirm("Are you sure to delete this customer?")
                        .then((result) => {
                            if (result.isConfirmed) {
                                updateCustomerStatus(c.accountNumber, 'D');
                            }
                        });
                });

                tbody.append(row);
            });
            filterCustomers(); // apply current filters after loading
        })
        .catch(error => NotificationService.error("Failed to load customers: " + error.message));
}

function updateCustomerStatus(accountNumber, newStatus) {
    fetch(baseURL + "customer", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ account_number: accountNumber, status: newStatus })
    })
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            NotificationService.success(data.message);
            loadAllCustomers();
        })
        .catch(error => NotificationService.error("Error: " + error.message));
}

function attachCustomerSearchFilters() {
    $("#searchCustomerAccountNumber, #searchCustomerName, #searchCustomerAddress, #searchCustomerTelephone").on("input", filterCustomers);
}

function filterCustomers() {
    const accFilter = $("#searchCustomerAccountNumber").val().toLowerCase();
    const nameFilter = $("#searchCustomerName").val().toLowerCase();
    const addressFilter = $("#searchCustomerAddress").val().toLowerCase();
    const telFilter = $("#searchCustomerTelephone").val().toLowerCase();

    $("#customerTableBody tr").each(function () {
        const cells = $(this).children();
        if (cells.length < 5) return; // skip malformed rows

        const match =
            cells.eq(0).text().toLowerCase().includes(accFilter) &&
            cells.eq(1).text().toLowerCase().includes(nameFilter) &&
            cells.eq(2).text().toLowerCase().includes(addressFilter) &&
            cells.eq(3).text().toLowerCase().includes(telFilter);
        $(this).toggle(match);
    });
}

// Generate account number when form loads
function generateAccountNumber() {
    fetch(baseURL + "customer?action=generateAccountNumber")
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            $("#customer-account-number").val(data.accountNumber);
        })
        .catch(error => NotificationService.error("Failed to generate account number: " + error.message));
}

// Call generate account number when page loads and form resets
$(document).ready(function() {
    generateAccountNumber();
});
