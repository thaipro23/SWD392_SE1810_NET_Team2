$(document).ready(function () {
    // State management
    let currentState = {
      page: 1,
      size: 10,
      keyword: "",
      status: "0"
    };

    // Initialize from URL parameters
    function initializeFromUrl() {
      const params = new URLSearchParams(window.location.search);
      currentState = {
        page: parseInt(params.get('page')) || 1,
        size: parseInt(params.get('size')) || 10,
        keyword: params.get('keyword') || "",
        status: params.get('status') || "0"
      };

      // Update UI elements
      $("#searchInput").val(currentState.keyword);
      $("#pageSizeSelect").val(currentState.size);
      if (currentState.status !== "0") {
        $("#statusFilterHeader").val(currentState.status);
      }
    }
    function loadPage() {
      $.ajax({
        url: window.location.pathname, // Use the current URI
        type: "GET", // Use GET for retrieving data
        data: {
          page: currentState.page,
          size: currentState.size,
          keyword: currentState.keyword,
          status: currentState.status
        },
        success: function (response) {
          $("#userTableBody").html(
            $(response).find("#userTableBody").html()
          );
          $("#pagination").html($(response).find("#pagination").html());
          $("#statusFilterHeader").val(currentState.status);
          // Check if no users were found
          if ($("#userTableBody").find("tr").length === 1) {
            $("#userTableBody").html(`
                        <tr>
                            <td colspan="8" class="text-center text-danger">No results found</td>
                        </tr>
                        `);
          }
            const newUrl = `${window.location.pathname}?page=${currentState.page}&size=${currentState.size}&keyword=${currentState.keyword}&status=${currentState.status}`;
          window.history.pushState({ path: newUrl }, "", newUrl);
          
          applyFilters();
        },
        error: function (xhr, status, error) {
          console.error("Error loading users:", status, error);
        },
      });
    }
    // Apply filters to the current table
    function applyFilters() {
      const rows = $("#userTableBody tbody tr");
      rows.each(function () {
        const row = $(this);
        const statusText = row.find("td:eq(5)").text().trim();

        const statusMatch = currentState.status === "0" || statusText === currentState.status;

        row.toggle(statusMatch);
      });
    }
    // Event Handlers
    $("#searchInput").on("keydown", function (event) {
      if (event.key === "Enter") {
        event.preventDefault();
        currentState.keyword = $(this).val().trim();
        currentState.page = 1; // Reset to first page
        loadPage();
      }
    });

    $(document).on("click", ".page-link", function (e) {
      e.preventDefault();
      const pageIndex = $(this).attr("data-page");
      if (pageIndex) {
        currentState.page = parseInt(pageIndex);
        loadPage();
      }
    });

    $("#pageSizeSelect").on("change", function () {
      currentState.size = parseInt($(this).val());
      currentState.page = 1; // Reset to first page
      loadPage();
    });

    // Override the original filter function
    window.filterTable = function () {
      currentState.status = $("#statusFilterHeader").val();
      currentState.page = 1; // Reset to first page
      loadPage();
    };

    // Initialize page
    initializeFromUrl();
    loadPage();
  });