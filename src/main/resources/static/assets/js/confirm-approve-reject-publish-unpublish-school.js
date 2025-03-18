document.querySelectorAll(".action-button").forEach((button) => {
    button.addEventListener("click", function () {
      const modalTitle = this.dataset.title;
      const modalMessage = this.dataset.message;
      const actionUrl = this.dataset.url;

      document.getElementById("modalTitle").innerText = modalTitle;
      document.getElementById("modalMessage").innerText = modalMessage;
      document
        .getElementById("confirmAction")
        .setAttribute("data-action-url", actionUrl);

      const modal = new bootstrap.Modal(
        document.getElementById("actionModal")
      );
      modal.show();
    });
  });

document
    .getElementById("confirmAction")
    .addEventListener("click", function () {
        const actionUrl = this.getAttribute("data-action-url");
        fetch(actionUrl, {
            method: "GET",
        }).then(async (response) => {
            if (response.ok) {
                location.reload();
            } else {
                const errorBody = await response.text();
                alert(`Action failed: ${errorBody}`);
            }
        });
    });

// confirmDeleteSchool
document
    .getElementById("confirmDeleteSchool")
    .addEventListener("click", function () {
        const actionUrl = this.getAttribute("data-url");
        fetch(actionUrl, {
            method: "DELETE",
        }).then(async (response) => {
            if (response.ok) {
                if (actionUrl.startsWith("/admin")) {
                    window.location.href = "/admin/schools";
                } else {
                    window.location.href = "/school-owner/schools";
                }
            } else {
                const errorBody = await response.text();
                alert(`Action failed: ${errorBody}`);
            }
        });
    });