function openEnrollModal(button) {
    const schoolId = button.getAttribute('data-school-id');
    document.getElementById('modalSchoolId').value = schoolId;
    const modal = new bootstrap.Modal(document.getElementById('enrollModal'));
    modal.show();
}

function cancelEnrollModal(button) {
    const schoolId = button.getAttribute('data-school-id');
    document.getElementById('cancelSchoolId').value = schoolId;
    const modal = new bootstrap.Modal(document.getElementById('cancelPendingModal'));
    modal.show();
}

// document.getElementById('enrollForm').addEventListener('submit', function (e) {
//     e.preventDefault();
//     const formData = new FormData(this);
//     fetch('/parent/pending', {
//         method: 'POST',
//         body: formData,
//     })
//         .then(response => response.text())
//         .then(result => {
//             if (result === "success") {
//                 const modal = bootstrap.Modal.getInstance(document.getElementById('enrollModal'));
//                 modal.hide();
//                 sessionStorage.setItem('alertMessage', 'success');
//                 sessionStorage.setItem('alertText', 'Your request has been submitted');
//                 location.reload();
//             } else {
//                 showAlert('danger', 'Failed to submit request. Please try again.');
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             showAlert('danger', 'An error occurred. Please try again.');
//         });
// });
document.getElementById('enrollForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const formData = new FormData(this);
    fetch('/parent/pending', {
        method: 'POST',
        body: formData,
    })
        .then(response => response.text())
        .then(result => {
            if (result === "success") {
                const modal = bootstrap.Modal.getInstance(document.getElementById('enrollModal'));
                modal.hide();

                const schoolId = formData.get("schoolId");
                const newEnrollStatus = 'PENDING';
                updateStatusUI(schoolId, newEnrollStatus);

                showAlert('success', 'Your request has been submitted');
            } else {
                showAlert('danger', 'Failed to submit request. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('danger', 'An error occurred. Please try again.');
        });
});


document.getElementById('cancelEnrollForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const formData = new FormData(this);
    fetch('/parent/cancel', {
        method: 'POST',
        body: formData,
    })
        .then(response => response.text())
        .then(result => {
            if (result === "success") {
                const modal = bootstrap.Modal.getInstance(document.getElementById('cancelPendingModal'));
                modal.hide();
                const schoolId = formData.get("schoolId");
                const newEnrollStatus = 'UNENROLL';
                updateStatusUI(schoolId, newEnrollStatus);

                showAlert('success', 'Your request has been cancelled');
            } else if (result === 'cancel-accepted-enroll') {
                const modal = bootstrap.Modal.getInstance(document.getElementById('cancelPendingModal'));
                modal.hide();
                const schoolId = formData.get("schoolId");
                const newEnrollStatus = 'ENROLL';
                updateStatusUI(schoolId, newEnrollStatus);
                showAlert('danger', 'Your request has been accepted by school owner. You cannot cancel request');
            }
            else if (result === 'cancel-cancelled-enroll') {
                const modal = bootstrap.Modal.getInstance(document.getElementById('cancelPendingModal'));
                modal.hide();
                const schoolId = formData.get("schoolId");
                const newEnrollStatus = 'CANCELLED';
                updateStatusUI(schoolId, newEnrollStatus);
                showAlert('danger', 'Your request has been cancelled by school owner. You cannot cancel request');
            }
            else {
                showAlert('danger', 'Failed to cancel request. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('danger', 'An error occurred. Please try again.');
        });
});

window.onload = function () {
    const alertMessage = sessionStorage.getItem('alertMessage');
    const alertText = sessionStorage.getItem('alertText');

    if (alertMessage && alertText) {
        showAlert(alertMessage, alertText);
        sessionStorage.removeItem('alertMessage');
        sessionStorage.removeItem('alertText');
    }
}
function updateStatusUI(schoolId, enrollStatus) {
    const container = document.querySelector(`[data-school-id="${schoolId}"]`).closest('.d-flex');

    if (!container) return;

    container.innerHTML = '';

    let buttonHTML = '';
    switch (enrollStatus) {
        case 'UNENROLL':
            buttonHTML += `
                <button class="btn btn-primary request-counseling-btn my-2"
                    onclick="fetchUserInfo()" data-school-id="${schoolId}">
                    Request Counseling
                </button>
                <button class="btn btn-primary enroll-btn my-2 mx-1"
                    onclick="openEnrollModal(this)" data-school-id="${schoolId}">
                    Enroll Now
                </button>`;
            break;
        case 'ENROLL':
            buttonHTML += `
                <button class="btn btn-primary enroll-btn my-2 mx-1" disabled>
                    Enrolled
                </button>`;
            break;
        case 'PENDING':
            buttonHTML += `
                <button class="btn btn-danger enroll-btn my-2 mx-1"
                    onclick="cancelEnrollModal(this)" data-school-id="${schoolId}">
                    Cancel request
                </button>`;
            break;
        case 'CANCELLED':
            buttonHTML += `
                <button class="btn btn-primary enroll-btn my-2 mx-1" disabled>
                    You can not enroll to this school!
                </button>`;
            break;
    }

    container.innerHTML = buttonHTML;
}