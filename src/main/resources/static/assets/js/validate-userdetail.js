function checkEmail() {
    var email = document.getElementById('email').value.trim();
    var eEmail = document.getElementById('eEmail');
    var reGexEmail = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (email === null || email === '') {
        eEmail.innerHTML = 'Email are required fields.';
    } else if (!reGexEmail.test(email)) {
        eEmail.innerHTML = 'Email is wrong format.';
    } else {
        eEmail.innerHTML = '';
    }
}

function checkPhoneNumber() {
    var phone = document.getElementById('phone').value.trim();
    var ePhone = document.getElementById('ePhone');
    var reGexPhone = /(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\b/;
    if (phone === null || phone === '') {
        ePhone.innerHTML = 'Phone are required fields.';
    } else if (!reGexPhone.test(phone)) {
        ePhone.innerHTML = 'Phone number is wrong format.';
    } else {
        ePhone.innerHTML = '';
    }

}

function checkInputIsEmpty(errorMessage) {
    var textinput = document.getElementByName('textinput')[0].value.trim();
    var texterr = document.getElementById('text-err');
    if (textinput === null || textinput === '') {
        texterr.innerHTML = errorMessage;
    } else {
        texterr.innerHTML = '';
    }
}