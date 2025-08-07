$(document).ready(function () {
    $('#dashboard-content').show();
    $('#customer-content, #item-content, #place-order-content, #order-content, #reports-content, #help-content').hide();

    $('.nav-link').click(function () {
        $('.nav-link').removeClass('active');
        $(this).addClass('active');
    });

    $('#manage-dashboard-tab').click(function () {
        $('#dashboard-content').show();
        $('#customer-content, #item-content, #place-order-content, #order-content, #reports-content, #help-content').hide();
    });

    $('#manage-customers-tab').click(function () {
        $('#customer-content').show();
        $('#dashboard-content, #item-content, #place-order-content, #order-content, #reports-content, #help-content').hide();
    });

    $('#manage-items-tab').click(function () {
        $('#item-content').show();
        $('#dashboard-content, #customer-content, #place-order-content, #order-content, #reports-content, #help-content').hide();
    });

    $('#manage-orders-tab').click(function () {
        $('#order-content').show();
        $('#dashboard-content, #customer-content, #item-content, #place-order-content, #reports-content, #help-content').hide();
    });

    $('#place-order-tab').click(function () {
        $('#place-order-content').show();
        $('#dashboard-content, #customer-content, #item-content, #order-content, #reports-content, #help-content').hide();
    });

    $('#reports-tab').click(function () {
        $('#reports-content').show();
        $('#dashboard-content, #customer-content, #item-content, #order-content, #place-order-content, #help-content').hide();
    });

    $('#help-section-tab').click(function () {
        $('#help-content').show();
        $('#dashboard-content, #customer-content, #item-content, #order-content, #place-order-content, #reports-content').hide();
    });
});