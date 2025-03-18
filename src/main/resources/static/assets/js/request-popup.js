document.getElementById('counselingForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const isNameValid = validateName();
    const isEmailValid = validateEmail();
    const isPhoneValid = validatePhone();
    const isInquiryValid = validateInquiry();

    if (isNameValid && isEmailValid && isPhoneValid && isInquiryValid) {
        const formData = new FormData(this);

        fetch('/parent/request', {
            method: 'POST',
            body: formData,
        })
            .then(response => response.text())
            .then(result => {
                if (result === "success") {
                    // Đóng modal
                    const modal = bootstrap.Modal.getInstance(document.getElementById('counselingModal'));
                    modal.hide();

                    // Hiển thị thông báo thành công
                    showAlert('success', 'Your request has been submitted');
                } else {
                    showAlert('danger', 'Failed to submit request. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert('danger', 'An error occurred. Please try again.');
            });
    }
});

function fetchUserInfo() {
    $.ajax({
        url: `/parent/request`,
        method: 'GET',
        success: function (data) {
            console.log('user info:', data);
            $('#fullName').val(data.fullname);
            $('#email').val(data.email);
            $('#phone').val(data.phone);
        },
        error: function (err) {
            const currentUri = window.location.href;
            window.location.href = `/auth/login?redirect=${encodeURIComponent(currentUri)}`;
            console.error('error fetch user info:', err);
        }
    });
}

// Hàm hiển thị alert sử dụng Bootstrap
function showAlert(type, message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 1051; max-width: 350px; border-radius: 8px; color: #66CC33;border: 1px solid #66CC33;';

    alertDiv.innerHTML = `
${message}
<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
`;

    document.body.appendChild(alertDiv);

    // Tự động ẩn sau 5 giây
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

function validateName() {
    const fullName = document.getElementById('fullName').value;
    const nameError = document.getElementById('nameError');

    if (!fullName || fullName.trim() === '') {
        nameError.textContent = 'Full name is required';
        document.getElementById('fullName').classList.add('is-invalid');
        return false;
    }
    nameError.textContent = '';
    document.getElementById('fullName').classList.remove('is-invalid');
    return true;
}

function validateEmail() {
    const email = document.getElementById('email').value;
    const emailError = document.getElementById('emailError');
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!email || email.trim() === '') {
        emailError.textContent = 'Email is required';
        document.getElementById('email').classList.add('is-invalid');
        return false;
    }

    if (!emailRegex.test(email)) {
        emailError.textContent = 'Email is invalid';
        document.getElementById('email').classList.add('is-invalid');
        return false;
    }

    emailError.textContent = '';
    document.getElementById('email').classList.remove('is-invalid');
    return true;
}

function validatePhone() {
    const phone = document.getElementById('phone').value;
    const phoneError = document.getElementById('phoneError');
    const phoneRegex = /^\+\d{1,3}\d{9,15}$/;

    if (!phone || phone.trim() === '') {
        phoneError.textContent = 'Phone number is required';
        document.getElementById('phone').classList.add('is-invalid');
        return false;
    }

    if (!phoneRegex.test(phone)) {
        phoneError.textContent = 'Phone number is invalid';
        document.getElementById('phone').classList.add('is-invalid');
        return false;
    }

    phoneError.textContent = '';
    document.getElementById('phone').classList.remove('is-invalid');
    return true;
}

function validateInquiry() {
    const inquiry = document.getElementById('inquiry').value;
    const inquiryError = document.getElementById('inquiryError');

    if (!inquiry || inquiry.trim() === '') {
        inquiryError.textContent = 'Inquiry is required';
        document.getElementById('inquiry').classList.add('is-invalid');
        return false;
    }

    inquiryError.textContent = '';
    document.getElementById('inquiry').classList.remove('is-invalid');
    return true;
}

document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll(".request-counseling-btn");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            // Lấy ID của trường từ thuộc tính `data-school-id`
            const schoolId = button.getAttribute("data-school-id");

            // Gán `schoolId` vào input ẩn trong form
            document.querySelector("#counselingForm input[name='id']").value = schoolId;

            // Hiển thị modal
            const modal = new bootstrap.Modal(document.getElementById("counselingModal"));
            modal.show();
        });
    });
});