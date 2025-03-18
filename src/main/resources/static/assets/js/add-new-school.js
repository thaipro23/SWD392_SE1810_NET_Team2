(function () {
  "use strict";
  var forms = document.querySelectorAll(".needs-validation");
  Array.prototype.slice.call(forms).forEach(function (form) {
    form.addEventListener(
      "submit",
      function (event) {
        if (!form.checkValidity()) {
          event.preventDefault();
          event.stopPropagation();
        }
        form.classList.add("was-validated");
      },
      false
    );
  });
})();

document.addEventListener("DOMContentLoaded", function () {
  const schoolNameInput = document.getElementById("school-name");
  const schoolCodeInput = document.getElementById("school-code");
  schoolNameInput.addEventListener("input", function () {
    const schoolName = schoolNameInput.value;
    if (schoolName.trim() !== "") {
      fetch(
        `/schools/add/get-school-code?name=${encodeURIComponent(schoolName)}`
      )
        .then((response) => response.text())
        .then((code) => {
          schoolCodeInput.value = code;
        })
        .catch((error) => console.error("Error fetching school code:", error));
    } else {
      schoolCodeInput.value = "";
    }
  });
});
