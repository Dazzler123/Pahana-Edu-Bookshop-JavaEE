$(document).ready(function () {
    $('#dashboard-content').show();
    $('#customer-content, #item-content').hide();

    $('.nav-link').click(function () {
        $('.nav-link').removeClass('active');
        $(this).addClass('active');
    });

    $('#manage-dashboard-tab').click(function () {
        $('#dashboard-content').show();
        $('#customer-content, #item-content').hide();
    });

    $('#manage-customers-tab').click(function () {
        $('#customer-content').show();
        $('#dashboard-content, #item-content').hide();
    });

    $('#manage-items-tab').click(function () {
        $('#item-content').show();
        $('#dashboard-content, #customer-content').hide();
    });
});