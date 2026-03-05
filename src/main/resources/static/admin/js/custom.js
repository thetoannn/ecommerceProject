document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector("#sidebar");
    if (!sidebar) return;

    const currentPath = window.location.pathname;
    sidebar.querySelectorAll(".nav-item[data-path]").forEach((item) => {
        const path = item.getAttribute("data-path");
        if (path && currentPath.startsWith(path)) {
            item.classList.add("active");
        }
    });
});

