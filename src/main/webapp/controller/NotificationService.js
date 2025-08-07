class NotificationService {

    // success notification
    static success(message) {
        return Swal.fire({
            icon: 'success',
            title: 'Success!',
            text: message,
            confirmButtonColor: '#28a745'
        });
    }
    
    // error notification
    static error(message) {
        return Swal.fire({
            icon: 'error',
            title: 'Error!',
            text: message,
            confirmButtonColor: '#dc3545'
        });
    }
    
    // confirmation dialog
    static confirm(message) {
        return Swal.fire({
            title: 'Confirmation',
            text: message,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#28a745',
            cancelButtonColor: '#dc3545',
            confirmButtonText: 'Yes',
            cancelButtonText: 'No'
        });
    }
    
    // loading notification
    static loading(message = 'Processing...') {
        Swal.fire({
            title: message,
            allowOutsideClick: false,
            allowEscapeKey: false,
            showConfirmButton: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
    }
    
    // close loading
    static closeLoading() {
        Swal.close();
    }
}

window.NotificationService = NotificationService;