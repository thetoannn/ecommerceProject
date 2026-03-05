// Utility functions for the E-Commerce application
const utils = {
    formatNumber: function (num) {
        if (!num) return "0";
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },

    escapeHtml: function (text) {
        if (!text) return "";
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    showCartMessage: function (message, type = "success") {
        let messageEl = document.getElementById("cartMessage");
        if (!messageEl) {
            messageEl = document.createElement("div");
            messageEl.id = "cartMessage";
            messageEl.className = `alert alert-${type === "error" ? "danger" : "success"} alert-dismissible fade show position-fixed`;
            messageEl.style.cssText = "top: 20px; right: 20px; z-index: 9999; min-width: 250px;";
            document.body.appendChild(messageEl);
        }

        messageEl.textContent = message;
        messageEl.className = `alert alert-${type === "error" ? "danger" : "success"} alert-dismissible fade show position-fixed`;

        setTimeout(() => {
            if (messageEl) {
                messageEl.classList.remove("show");
                setTimeout(() => messageEl.remove(), 150);
            }
        }, 3000);
    }
};

window.utils = utils;
