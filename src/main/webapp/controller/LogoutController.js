$(document).ready(function() {
    $('#user-logout-tab').on('click', function(e) {
        e.preventDefault();
        
        $.ajax({
            url: baseURL + 'logout',
            method: 'POST',
            complete: function() {
                localStorage.clear();
                window.location.href = 'login.html';
            }
        });
    });
});