$(document).ready(function() {
    $('#user-logout-tab').on('click', function(e) {
        e.preventDefault();
        
        NotificationService.confirm("Are you sure you want to logout?")
            .then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: baseURL + 'logout',
                        method: 'POST',
                        complete: function() {
                            localStorage.clear();
                            window.location.href = 'login.html';
                        }
                    });
                }
            });
    });
});