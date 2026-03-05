// Notification logic
const notificationModule = {
    init: function () {
        const offcanvasNotifications = document.getElementById("offcanvasNotifications");
        const markAllReadBtn = document.getElementById("markAllReadBtn");

        if (offcanvasNotifications && window.isLoggedIn) {
            offcanvasNotifications.addEventListener("show.bs.offcanvas", this.loadNotifications.bind(this));
        }

        if (markAllReadBtn) {
            markAllReadBtn.addEventListener('click', () => {
                fetch("/notifications/api/mark-all-read", { method: "POST" })
                    .then(() => {
                        this.updateNotificationCount();
                        this.loadNotifications();
                    });
            });
        }

        if (window.isLoggedIn) {
            this.updateNotificationCount();
            setInterval(this.updateNotificationCount.bind(this), 30000);
        }
    },

    loadNotifications: function () {
        const notificationsList = document.getElementById("notificationsList");
        if (!notificationsList) return;

        notificationsList.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-secondary" role="status"></div></div>';

        fetch("/notifications/api/recent?limit=10")
            .then(res => res.status === 401 ? Promise.reject("401") : res.json())
            .then(notifications => {
                if (notifications.length === 0) {
                    notificationsList.innerHTML = '<div class="text-center py-4"><p class="text-muted">Không có thông báo mới</p></div>';
                    return;
                }
                this.renderNotifications(notifications, notificationsList);
            })
            .catch(err => {
                notificationsList.innerHTML = `<div class="text-center py-4"><p class="text-danger">${err === "401" ? "Vui lòng đăng nhập" : "Lỗi khi tải thông báo"}</p></div>`;
            });
    },

    renderNotifications: function (notifications, container) {
        let html = '<div class="list-group list-group-flush">';
        notifications.forEach(n => {
            const typeClass = n.type === 'ORDER' ? 'bg-info text-dark' : n.type === 'PROMOTION' ? 'bg-warning text-dark' : 'bg-secondary';
            const isRead = n.isRead || n.read;
            const unreadStyle = !isRead ? 'border-left: 3px solid #3b82f6; background-color: rgba(59, 130, 246, 0.08);' : '';

            html += `
                <div class="list-group-item border-bottom notification-item" data-id="${n.id}" data-link="${n.link || ''}" data-read="${isRead}" style="${unreadStyle} cursor: pointer;">
                    <div class="d-flex justify-content-between align-items-start py-2">
                        <div class="flex-grow-1">
                            <div class="d-flex align-items-center mb-1">
                                <h6 class="mb-0 me-2 small">${window.utils.escapeHtml(n.title)}</h6>
                                ${!isRead ? '<span class="badge bg-primary me-1 small">Mới</span>' : ''}
                                <span class="badge ${typeClass} small">${n.type}</span>
                            </div>
                            <p class="mb-1 small text-muted">${window.utils.escapeHtml(n.message)}</p>
                            <small class="text-muted">${new Date(n.createdAt).toLocaleString('vi-VN')}</small>
                        </div>
                    </div>
                </div>`;
        });
        html += '</div>';
        container.innerHTML = html;

        container.querySelectorAll('.notification-item').forEach(item => {
            item.addEventListener('click', () => {
                const id = item.dataset.id;
                const link = item.dataset.link;
                if (item.dataset.read === 'false') {
                    this.markAsRead(id, link);
                } else if (link) {
                    window.location.href = link;
                }
            });
        });
    },

    updateNotificationCount: function () {
        if (!window.isLoggedIn) return;
        fetch("/notifications/api/unread-count")
            .then(res => res.json())
            .then(data => {
                const badge = document.querySelector(".notification-badge");
                const countValue = document.getElementById("unreadCountValue");
                const countText = document.getElementById("unreadCountText");

                if (data.count > 0) {
                    const displayCount = data.count > 99 ? '99+' : data.count;
                    if (badge) { badge.textContent = displayCount; badge.style.display = 'block'; }
                    if (countValue) countValue.textContent = data.count;
                    if (countText) countText.style.display = 'block';
                } else {
                    if (badge) badge.style.display = 'none';
                    if (countText) countText.style.display = 'none';
                }
            }).catch(() => { });
    },

    markAsRead: function (id, link) {
        fetch(`/notifications/api/mark-read/${id}`, { method: "POST" })
            .then(() => {
                this.updateNotificationCount();
                if (link) window.location.href = link; else this.loadNotifications();
            });
    }
};

window.notificationModule = notificationModule;
