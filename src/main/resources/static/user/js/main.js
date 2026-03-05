document.addEventListener("DOMContentLoaded", () => {
  window.isLoggedIn = document.getElementById("userMenu") !== null;

  const yearEl = document.querySelector("#currentYear");
  if (yearEl) {
    yearEl.textContent = new Date().getFullYear();
  }

  const smoothLinks = document.querySelectorAll("a[href^='#']:not(.search-button)");
  smoothLinks.forEach(link => {
    link.addEventListener("click", (event) => {
      const targetId = link.getAttribute("href").substring(1);
      const targetElement = document.getElementById(targetId);
      if (targetElement) {
        event.preventDefault();
        targetElement.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    });
  });

  if (window.searchModule) {
    window.searchModule.init();
  }

  if (window.notificationModule) {
    window.notificationModule.init();
  }

  if (window.cartModule) {
    window.cartModule.init();
  }
});
