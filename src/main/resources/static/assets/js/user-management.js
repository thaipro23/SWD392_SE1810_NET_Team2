  $(document).ready(function () {
        // State management
        let currentState = {
          page: 1,
          size: 10,
          keyword: "",
          roleFilter: "",
          statusFilter: ""
        };

        // Initialize from URL parameters
        function initializeFromUrl() {
          const params = new URLSearchParams(window.location.search);
          currentState = {
            page: parseInt(params.get('page')) || 1,
            size: parseInt(params.get('size')) || 10,
            keyword: params.get('keyword') || "",
            roleFilter: params.get('role') || "0",
            statusFilter: params.get('status') || "0"
          };

          // Update UI elements
          $("#searchInput").val(currentState.keyword);
          $("#pageSizeSelect").val(currentState.size);
          if (currentState.roleFilter !== "0") {
            $("#roleFilterHeader").val(currentState.roleFilter);
          }
          if (currentState.statusFilter !== "0") {
            $("#statusFilterHeader").val(currentState.statusFilter);
          }
        }

        // Load page with current state
        function loadPage() {
          $.ajax({
            url: "/admin/users",
            type: "GET",
            data: {
              page: currentState.page,
              size: currentState.size,
              keyword: currentState.keyword,
              role: currentState.roleFilter,
              status: currentState.statusFilter
            },
            success: function (response) {
              $("#userTableBody").html($(response).find("#userTableBody").html());
              $("#pagination").html($(response).find("#pagination").html());
              $("#roleFilterHeader").val(currentState.roleFilter);
              $("#statusFilterHeader").val(currentState.statusFilter);
              // Check for empty results
              if ($("#userTableBody tbody tr").length === 0) {
                $("#userTableBody tbody").html(`
                  <tr>
                    <td colspan="8" class="text-center text-danger">No results found</td>
                  </tr>
                `);
              }

              // Update URL
              const newUrl = `/admin/users?page=${currentState.page}&size=${currentState.size}&keyword=${currentState.keyword}&role=${currentState.roleFilter}&status=${currentState.statusFilter}`;
              window.history.pushState({ path: newUrl }, "", newUrl);

              // Apply current filters to the loaded content
              applyFilters();
            },
            error: function (xhr, status, error) {
              console.error("Error loading users:", error);
            }
          });
        }

        // Apply filters to the current table
        function applyFilters() {
          const rows = $("#userTableBody tbody tr");
          rows.each(function () {
            const row = $(this);
            const roleText = row.find("td:eq(5)").text().trim();
            const statusText = row.find("td:eq(6)").text().trim();

            const roleMatch = currentState.roleFilter === "0" || roleText === currentState.roleFilter;
            const statusMatch = currentState.statusFilter === "0" || statusText === currentState.statusFilter;

            row.toggle(roleMatch && statusMatch);
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
          currentState.roleFilter = $("#roleFilterHeader").val();
          currentState.statusFilter = $("#statusFilterHeader").val();
          currentState.page = 1; // Reset to first page
          loadPage();
        };
        // Initialize page
        initializeFromUrl();
        loadPage();
      });