function openDeleteModal(id) {
    const confirmDeleteButton =
      document.getElementById("confirmDeleteSchool");
    confirmDeleteButton.setAttribute("data-user-id", id);
    $("#deleteUserSchool").modal("show");
  }
  function deleteUser(id) {
    const currentPath = window.location.pathname;
    $.ajax({
      url: currentPath + '/detail/'+id+'/delete',
      type: "DELETE",
      success: function () {
        $("#deleteUserSchool").modal("hide");
        location.reload();
      },
      error: function (xhr, status, error) {
        console.error("Error deleting user:", error);
        showToast("Error deleting user: " + error); 
      },
    });
  }

  // Sử dụng hàm khi nhấp vào nút xác nhận xóa
  $("#confirmDeleteSchool").on("click", function () {
    const id = $(this).data("user-id");
    deleteUser(id);
  });