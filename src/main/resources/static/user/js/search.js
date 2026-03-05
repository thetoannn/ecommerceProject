// Search popup logic
const searchModule = {
    init: function () {
        const searchButton = document.querySelector(".search-button");
        const searchPopup = document.getElementById("searchPopup");

        if (!searchButton || !searchPopup) return;

        searchButton.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();

            searchPopup.classList.toggle("is-visible");
            searchButton.dataset.justClicked = 'true';
            setTimeout(() => delete searchButton.dataset.justClicked, 100);

            if (searchPopup.classList.contains("is-visible")) {
                setTimeout(() => {
                    const searchInput = document.getElementById("search-form");
                    if (searchInput) searchInput.focus();
                }, 100);
            }
        });

        document.addEventListener("click", (e) => {
            if (searchButton.dataset.justClicked === 'true') return;
            if (searchPopup.classList.contains("is-visible") &&
                !searchPopup.contains(e.target) &&
                !searchButton.contains(e.target)) {
                searchPopup.classList.remove("is-visible");
            }
        });

        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape" && searchPopup.classList.contains("is-visible")) {
                searchPopup.classList.remove("is-visible");
            }
        });
    }
};

window.searchModule = searchModule;
