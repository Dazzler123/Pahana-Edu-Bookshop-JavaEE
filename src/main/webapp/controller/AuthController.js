$(document).ready(function() {
    if (localStorage.getItem('isLoggedIn') !== 'true') {
        window.location.href = 'login.html';
        return;
    }
    
    const username = localStorage.getItem('username');
    if (username) {
        $('#usernameDisplay').text(username);
    }
});
