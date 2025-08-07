$(document).ready(function () {
    loadAllItems();
    attachItemSearchFilters();

    $("#btn-reset-item-filters").on("click", function () {
        $("#searchItemCode, #searchItemName, #searchItemUnitPrice, #searchItemQtyOnHand").val("");
        filterItems();
        $("#searchItemCode").focus();
    });

    $("#btn-reset-item-form").on("click", function () {
        resetItemFormUI();
    });
});

let isItemUpdateMode = false;
let currentItemStatus = null;

// create item
$("#addItemForm").on("submit", function (e) {
    e.preventDefault();
    const form = $(this)[0];
    const formData = new FormData(form);
    if (isItemUpdateMode) formData.set("status", currentItemStatus);
    const formParams = new URLSearchParams(formData).toString();

    fetch(baseURL + "item", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formParams
    })
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            NotificationService.success(data.message);
            form.reset();
            resetItemFormUI();
            loadAllItems();
        })
        .catch(error => NotificationService.error("Error: " + error.message));
});

function resetItemFormUI() {
    isItemUpdateMode = false;
    currentItemStatus = null;
    $("#add-new-item-form-header").text("Add New Item");
    $("#btn-create-item").text("Add Item").removeClass("btn-warning").addClass("btn-success");
    $("#item_code").prop("readOnly", false);
}

function loadAllItems() {
    fetch(baseURL + "item")
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            const tbody = $("#itemTableBody");
            tbody.empty();
            data.items.forEach(c => {
                const statusMap = { A: "Active", I: "Inactive", D: "Deleted" };
                const statusClass = { A: "bg-success", I: "bg-secondary", D: "bg-danger" };
                const row = $(`
                    <tr>
                        <td>${c.itemCode}</td>
                        <td>${c.name}</td>
                        <td>${c.unitPrice}</td>
                        <td>${c.qtyOnHand}</td>
                        <td><span class="badge ${statusClass[c.status] || 'bg-dark'}">${statusMap[c.status] || 'Unknown'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-success activate-btn" ${c.status !== 'I' ? 'disabled' : ''}>Activate</button>
                            <button class="btn btn-sm btn-warning inactivate-btn" ${c.status !== 'A' ? 'disabled' : ''}>Inactivate</button>
                            <button class="btn btn-sm btn-primary edit-btn" ${c.status === 'D' ? 'disabled' : ''}>Edit</button>
                            <button class="btn btn-sm btn-danger delete-btn" ${c.status === 'D' ? 'disabled' : ''}>Delete</button>
                        </td>
                    </tr>`);

                row.find(".edit-btn").on("click", () => {
                    $("#add-new-item-form-header").text("Update Item");
                    $("#item_code").val(c.itemCode).prop("readOnly", true);
                    $("#item_name").val(c.name);
                    $("#item_unit_price").val(c.unitPrice);
                    $("#item_qty_on_hand").val(c.qtyOnHand);
                    $("#item-status").val(c.status);
                    isItemUpdateMode = true;
                    currentItemStatus = c.status;
                    $("#btn-create-item").text("Update Item").removeClass("btn-success").addClass("btn-warning");
                });

                row.find(".activate-btn").on("click", () => updateItemStatus(c.itemCode, 'A'));
                row.find(".inactivate-btn").on("click", () => {
                    NotificationService.confirm("Are you sure to inactivate this item?")
                        .then((result) => {
                            if (result.isConfirmed) {
                                updateItemStatus(c.itemCode, 'I');
                            }
                        });
                });
                row.find(".delete-btn").on("click", () => {
                    NotificationService.confirm("Are you sure to delete this item?")
                        .then((result) => {
                            if (result.isConfirmed) {
                                updateItemStatus(c.itemCode, 'D');
                            }
                        });
                });

                tbody.append(row);
            });
            filterItems(); // apply current filters after loading
        })
        .catch(error => NotificationService.error("Failed to load items: " + error.message));
}

function updateItemStatus(itemCode, newStatus) {
    fetch(baseURL + "item", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ item_code: itemCode, status: newStatus })
    })
        .then(res => res.ok ? res.json() : res.json().then(err => Promise.reject(err)))
        .then(data => {
            NotificationService.success(data.message);
            loadAllItems();
        })
        .catch(error => NotificationService.error("Error: " + error.message));
}

function attachItemSearchFilters() {
    $("#searchItemCode, #searchItemName, #searchItemUnitPrice, #searchItemQtyOnHand").on("input", filterItems);
}

function filterItems() {
    const codeFilter = $("#searchItemCode").val().toLowerCase();
    const nameFilter = $("#searchItemName").val().toLowerCase();
    const priceFilter = $("#searchItemUnitPrice").val().toLowerCase();
    const qtyFilter = $("#searchItemQtyOnHand").val().toLowerCase();

    $("#itemTableBody tr").each(function () {
        const cells = $(this).children();
        if (cells.length < 5) return; // skip malformed rows

        const match =
            cells.eq(0).text().toLowerCase().includes(codeFilter) &&
            cells.eq(1).text().toLowerCase().includes(nameFilter) &&
            cells.eq(2).text().toLowerCase().includes(priceFilter) &&
            cells.eq(3).text().toLowerCase().includes(qtyFilter);
        $(this).toggle(match);
    });
}