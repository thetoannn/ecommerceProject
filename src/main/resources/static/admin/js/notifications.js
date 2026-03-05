document.addEventListener('DOMContentLoaded', function() {
    const notificationDropdown = document.getElementById('notificationDropdown');
    const notificationsList = document.getElementById('adminNotificationsList');
    
    // Load notifications when dropdown opens
    if (notificationDropdown && notificationsList) {
        notificationDropdown.addEventListener('click', function(e) {
            e.preventDefault();
            loadAdminNotifications();
        });
    }
    
    // Update notification count periodically
    setInterval(updateAdminNotificationCount, 30000); // Every 30 seconds
    updateAdminNotificationCount();
    
    function loadAdminNotifications() {
        if (!notificationsList) return;
        
        notificationsList.innerHTML = '<div class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary" role="status"><span class="sr-only">Loading...</span></div></div>';
        
        fetch("/notifications/api/recent?limit=5")
            .then(response => response.json())
            .then(notifications => {
                if (notifications.length === 0) {
                    notificationsList.innerHTML = '<div class="text-center py-3"><p class="text-muted small mb-0">Không có thông báo mới</p></div>';
                } else {
                    let html = '';
                    notifications.forEach(notification => {
                        const typeClass = notification.type === 'ORDER' ? 'bg-info' :
                                         notification.type === 'PROMOTION' ? 'bg-warning' : 'bg-secondary';
                        const date = new Date(notification.createdAt);
                        const formattedDate = formatDate(date);
                        const iconClass = notification.type === 'ORDER' ? 'ti-shopping-cart' :
                                        notification.type === 'PROMOTION' ? 'ti-gift' : 'ti-info-alt';
                        const notificationLink = notification.link || '/notifications';

                        html += `
                            <a class="dropdown-item preview-item notification-item" href="${notificationLink}" data-id="${notification.id}" style="cursor: pointer;">
                                <div class="preview-thumbnail">
                                    <div class="preview-icon ${typeClass}">
                                        <i class="${iconClass} mx-0"></i>
                                    </div>
                                </div>
                                <div class="preview-item-content">
                                    <h6 class="preview-subject font-weight-normal">${escapeHtml(notification.title)}</h6>
                                    <p class="font-weight-light small-text mb-0 text-muted">${escapeHtml(notification.message)}</p>
                                    <p class="font-weight-light small-text mb-0 text-muted">${formattedDate}</p>
                                </div>
                            </a>
                        `;
                    });
                    notificationsList.innerHTML = html;
                    
                    // Add click handler to mark as read
                    notificationsList.querySelectorAll('.notification-item').forEach(item => {
                        item.addEventListener('click', function(e) {
                            const notificationId = this.getAttribute('data-id');
                            if (notificationId) {
                                markAsRead(notificationId);
                            }
                        });
                    });
                }
            })
            .catch(error => {
                console.error('Error loading notifications:', error);
                notificationsList.innerHTML = '<div class="text-center py-3"><p class="text-danger small mb-0">Lỗi khi tải thông báo</p></div>';
            });
    }
    
    function updateAdminNotificationCount() {
        fetch("/notifications/api/unread-count")
            .then(response => response.json())
            .then(data => {
                const countElement = document.querySelector('#notificationDropdown .count');
                if (data.count > 0) {
                    if (countElement) {
                        countElement.textContent = data.count > 99 ? '99+' : data.count;
                        countElement.style.display = 'inline-block';
                    }
                } else {
                    if (countElement) {
                        countElement.style.display = 'none';
                    }
                }
            })
            .catch(error => console.error('Error updating notification count:', error));
    }
    
    function markAsRead(id) {
        fetch("/notifications/api/mark-read/" + id, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateAdminNotificationCount();
                loadAdminNotifications();
            }
        })
        .catch(error => console.error('Error marking as read:', error));
    }
    
    function formatDate(date) {
        const now = new Date();
        const diff = now - date;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);
        
        if (minutes < 1) return 'Vừa xong';
        if (minutes < 60) return minutes + ' phút trước';
        if (hours < 24) return hours + ' giờ trước';
        if (days < 7) return days + ' ngày trước';
        
        return date.toLocaleDateString('vi-VN', { 
            day: '2-digit', 
            month: '2-digit', 
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
    
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});

