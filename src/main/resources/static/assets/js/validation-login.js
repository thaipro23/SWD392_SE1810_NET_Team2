function validateInput() {
  const input = document.getElementById("email").value;
  const emailField = document.getElementById("email");
  const usernameError = document.getElementById("usernameError");
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  usernameError.textContent = "";

  if (input.includes("@")) {
    if (!emailPattern.test(input)) {
      usernameError.textContent = "Please enter a valid email address.";
      emailField.classList.add("is-invalid");
      return false;
    } else {
      usernameError.textContent = "This field is required.";
      emailField.classList.remove("is-invalid");
    }
  } else {
    usernameError.textContent = "This field is required.";
    if (input.includes(" ")) {
      emailField.classList.add("is-invalid");
      return false;
    } else {
      emailField.classList.remove("is-invalid");
    }
  }
  return true;
}
function validatePassword() {
  const password = document.getElementById("password");
  if (password.value.includes(" ")) {
    password.classList.add("is-invalid");
    return false;
  } else {
    password.classList.remove("is-invalid");
  }
  return true;
}

document.getElementById("email").addEventListener("input", validateInput);
document.getElementById("password").addEventListener("input", validatePassword);

var forms = document.querySelectorAll(".needs-validation");
Array.prototype.slice.call(forms).forEach(function (form) {
  form.addEventListener(
    "submit",
    function (event) {
      if (!form.checkValidity() || !validateInput() || !validatePassword()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add("was-validated");
    },
    false
  );
});
