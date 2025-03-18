function openDeleteModal(userId) {
    const confirmDeleteButton =
      document.getElementById("confirmDeleteUser");
    confirmDeleteButton.setAttribute("data-user-id", userId);
    $("#deleteUserModal").modal("show");
  }
  function deleteUser(userId) {
    $.ajax({
      url: "/admin/users/" + userId,
      type: "DELETE",
      success: function () {
        $("#deleteUserModal").modal("hide");
        location.reload();
      },
      error: function (xhr, status, error) {
        console.error("Error deleting user:", error);
        showToast("Error deleting user: " + error); 
      },
    });
  }

  // Sử dụng hàm khi nhấp vào nút xác nhận xóa
  $("#confirmDeleteUser").on("click", function () {
    const userId = $(this).data("user-id");
    deleteUser(userId);
  });