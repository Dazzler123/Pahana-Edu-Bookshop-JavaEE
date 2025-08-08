$(document).ready(function() {
    // Check if already logged in
    if (localStorage.getItem('isLoggedIn') === 'true') {
        window.location.href = 'index.html';
        return;
    }

    const loginForm = $('#loginForm');
    const errorMessage = $('#errorMessage');

    loginForm.on('submit', function(e) {
        e.preventDefault();
        
        const username = $('#username').val();
        const password = $('#password').val();

        $.ajax({
            url: baseURL + 'login',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                username: username,
                password: password
            }),
            success: function(data) {
                if (data.success) {
                    // Store login state in localStorage
                    localStorage.setItem('isLoggedIn', 'true');
                    localStorage.setItem('username', data.username);
                    
                    // Redirect to main page
                    window.location.href = 'index.html';
                } else {
                    showError(data.message);
                }
            },
            error: function(xhr) {
                const response = xhr.responseJSON;
                showError(response ? response.message : 'Login failed. Please try again.');
            }
        });
    });

    function showError(message) {
        errorMessage.text(message);
        errorMessage.show();
        setTimeout(() => {
            errorMessage.hide();
        }, 5000);
    }
});