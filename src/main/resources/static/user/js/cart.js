const cartModule = {
    init: function () {
        const offcanvasCart = document.getElementById("offcanvasCart");

        if (offcanvasCart) {
            offcanvasCart.addEventListener("show.bs.offcanvas", this.loadCart.bind(this));
        }

        document.addEventListener("submit", (e) => {
            const form = e.target;
            if (form && form.action && form.action.includes("/cart/add/")) {
                e.preventDefault();
                const apiAction = form.action.replace("/cart/add/", "/api/cart/add/");
                fetch(apiAction, {
                    method: "POST",
                    body: new FormData(form),
                    headers: {
                        "X-Requested-With": "XMLHttpRequest"
                    }
                })
                    .then(res => {
                        if (res.status === 401) {
                            window.location.href = "/login";
                            return;
                        }
                        return res.json();
                    })
                    .then(data => {
                        if (data) {
                            if (data.success) {
                                this.updateCart();
                                window.utils.showCartMessage(data.message || "Đã thêm vào giỏ hàng");
                            } else {
                                window.utils.showCartMessage(data.message || "Có lỗi xảy ra", "error");
                            }
                        }
                    })
                    .catch(() => window.utils.showCartMessage("Có lỗi xảy ra", "error"));
            }
        });

        if (window.isLoggedIn) {
            this.updateCart();
        } else {
            this.updateCartBadge(0);
        }
    },

    loadCart: function () {
        if (!window.isLoggedIn) {
            this.updateCartUI({ authenticated: false });
            return;
        }
        fetch("/api/cart")
            .then(res => res.status === 401 ? { authenticated: false } : res.json())
            .then(data => this.updateCartUI(data))
            .catch(() => this.updateCartUI({ error: true }));
    },

    updateCart: function () {
        if (!window.isLoggedIn) {
            this.updateCartBadge(0);
            return;
        }
        fetch("/api/cart")
            .then(res => res.status === 401 ? { authenticated: false } : res.json())
            .then(data => {
                this.updateCartUI(data);
                this.updateCartBadge(data.itemCount || 0);
            });
    },

    updateCartUI: function (data) {
        const cartContent = document.getElementById("cartContent");
        const cartAction = document.getElementById("cartAction");
        const cartBadgeHeader = document.getElementById("cartBadge");

        if (!cartContent) return;

        if (data.authenticated === false) {
            cartContent.innerHTML = '<div class="text-center py-4"><p class="text-muted">Vui lòng đăng nhập để sử dụng giỏ hàng</p></div>';
            if (cartAction) cartAction.innerHTML = '<a href="/login" class="w-100 btn btn-primary btn-lg">Đăng nhập</a>';
            this.updateCartBadge(0);
            return;
        }

        if (data.error) {
            cartContent.innerHTML = '<div class="text-center py-4"><p class="text-danger">Lỗi khi tải giỏ hàng</p></div>';
            return;
        }

        if (data.items && data.items.length > 0) {
            let html = '<ul class="list-group mb-3">';
            data.items.forEach(item => {
                html += `
                    <li class="list-group-item lh-sm">
                        <div class="d-flex justify-content-between align-items-start">
                            <div class="flex-grow-1">
                                <h6 class="my-0 small fw-bold">${window.utils.escapeHtml(item.productName)}</h6>
                                <div class="d-flex align-items-center gap-2 my-2">
                                    <button class="btn btn-xs btn-outline-secondary py-0 px-2 update-qty" data-id="${item.productId}" data-qty="${item.quantity - 1}" ${item.quantity <= 1 ? "disabled" : ""}>-</button>
                                    <span class="badge bg-secondary">${item.quantity}</span>
                                    <button class="btn btn-xs btn-outline-secondary py-0 px-2 update-qty" data-id="${item.productId}" data-qty="${item.quantity + 1}">+</button>
                                    <button class="btn btn-link text-danger p-0 ms-2 small remove-item" data-id="${item.productId}" style="text-decoration:none; font-size:0.75rem;">Xóa</button>
                                </div>
                            </div>
                            <span class="text-body-secondary small">${window.utils.formatNumber(item.subtotal)} ₫</span>
                        </div>
                    </li>`;
            });
            html += `
                <li class="list-group-item d-flex justify-content-between bg-light">
                    <span class="fw-bold">Tổng cộng</span>
                    <strong class="text-primary">${window.utils.formatNumber(data.total)} ₫</strong>
                </li></ul>`;
            cartContent.innerHTML = html;

            if (cartBadgeHeader) cartBadgeHeader.textContent = data.itemCount;
            if (cartAction) cartAction.innerHTML = '<a href="/checkout" class="w-100 btn btn-primary btn-lg">Thanh toán</a>';

            cartContent.querySelectorAll(".update-qty").forEach(btn => {
                btn.addEventListener("click", () => this.updateItemQuantity(btn.dataset.id, btn.dataset.qty));
            });
            cartContent.querySelectorAll(".remove-item").forEach(btn => {
                btn.addEventListener("click", () => this.removeItem(btn.dataset.id));
            });
        } else {
            cartContent.innerHTML = '<div class="text-center py-4"><p class="text-muted">Giỏ hàng trống</p></div>';
            if (cartBadgeHeader) cartBadgeHeader.textContent = "0";
            if (cartAction) cartAction.innerHTML = '<a href="/products" class="w-100 btn btn-outline-primary btn-lg">Mua sắm ngay</a>';
        }
    },

    updateItemQuantity: function (id, qty) {
        fetch(`/api/cart/update/${id}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "X-Requested-With": "XMLHttpRequest"
            },
            body: `qty=${qty}`
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    this.updateCart();
                } else {
                    window.utils.showCartMessage(data.message || "Lỗi cập nhật số lượng", "error");
                }
            })
            .catch(() => window.utils.showCartMessage("Lỗi kết nối", "error"));
    },

    removeItem: function (id) {
        fetch(`/cart/remove/${id}`, { method: "POST" })
            .then(res => res.ok ? this.updateCart() : window.utils.showCartMessage("Lỗi khi xóa", "error"));
    },

    updateCartBadge: function (count) {
        const cartBadge = document.querySelector(".cart-badge");
        if (count > 0) {
            if (cartBadge) { cartBadge.textContent = count > 99 ? "99+" : count; cartBadge.style.display = "block"; }
        } else if (cartBadge) {
            cartBadge.style.display = "none";
        }
    }
};

window.cartModule = cartModule;
