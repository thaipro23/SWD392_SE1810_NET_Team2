document.addEventListener("DOMContentLoaded", function () {
    const hash = window.location.hash;
    if (hash) {
      const tabTrigger = document.querySelector(`button[data-bs-target="${hash}"]`);
      if (tabTrigger) {
        const tab = new bootstrap.Tab(tabTrigger);
        tab.show();
        tabTrigger.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    }
    const tabButtons = document.querySelectorAll('button[data-bs-toggle="tab"]');
    tabButtons.forEach(button => {
      button.addEventListener('shown.bs.tab', function (event) {
        const target = event.target.getAttribute('data-bs-target');
        history.replaceState(null, null, target);
      });
    });
  });