// 访问监控系统前端逻辑
class AccessMonitoringApp {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8080/api';
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadSystemInfo();
        this.loadAccessHistory();
    }

    bindEvents() {
        // 添加用户表单提交
        const addUserForm = document.getElementById('add-user-form');
        addUserForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.addUser();
        });

        // 刷新按钮
        const refreshBtn = document.getElementById('refresh-btn');
        refreshBtn.addEventListener('click', () => {
            this.loadAccessHistory();
        });

        // 删除确认对话框
        const confirmDeleteBtn = document.getElementById('confirm-delete');
        const cancelDeleteBtn = document.getElementById('cancel-delete');

        confirmDeleteBtn.addEventListener('click', () => {
            const userId = document.getElementById('delete-user-id').textContent;
            this.deleteUser(userId);
            this.hideDeleteModal();
        });

        cancelDeleteBtn.addEventListener('click', () => {
            this.hideDeleteModal();
        });
    }

    async loadSystemInfo() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/system/info`);
            const data = await response.json();

            document.getElementById('system-name').textContent = data.systemName;
            document.getElementById('user-count').textContent = `用户: ${data.currentUsers}/${data.capacity}`;
        } catch (error) {
            console.error('加载系统信息失败:', error);
            this.showMessage('加载系统信息失败', 'error');
        }
    }

    async loadAccessHistory() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/access-history`);
            const data = await response.json();

            this.renderAccessHistory(data);
            this.loadSystemInfo(); // 同时更新系统信息
        } catch (error) {
            console.error('加载访问历史失败:', error);
            this.showMessage('加载访问历史失败', 'error');
        }
    }

    renderAccessHistory(histories) {
        const tbody = document.getElementById('access-history-body');

        if (histories.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="no-data">暂无访问记录</td></tr>';
            return;
        }

        tbody.innerHTML = histories.map(history => `
            <tr>
                <td>${this.escapeHtml(history.userId)}</td>
                <td>${history.formattedTime}</td>
                <td>
                    <button class="delete-btn" onclick="app.showDeleteModal('${this.escapeHtml(history.userId)}')">
                        删除
                    </button>
                </td>
            </tr>
        `).join('');
    }

    async addUser() {
        const userIdInput = document.getElementById('user-id');
        const userId = userIdInput.value.trim();

        if (!userId) {
            this.showMessage('请输入用户ID', 'error');
            return;
        }

        try {
            const response = await fetch(`${this.apiBaseUrl}/access-history`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userId: userId })
            });

            const data = await response.json();

            if (data.success) {
                this.showMessage(data.message, 'success');
                userIdInput.value = '';
                this.loadAccessHistory();
            } else {
                this.showMessage(data.message, 'error');
            }
        } catch (error) {
            console.error('添加用户失败:', error);
            this.showMessage('添加用户失败，请重试', 'error');
        }
    }

    showDeleteModal(userId) {
        document.getElementById('delete-user-id').textContent = userId;
        document.getElementById('delete-modal').classList.add('show');
    }

    hideDeleteModal() {
        document.getElementById('delete-modal').classList.remove('show');
    }

    async deleteUser(userId) {
        try {
            const response = await fetch(`${this.apiBaseUrl}/access-history/${encodeURIComponent(userId)}`, {
                method: 'DELETE'
            });

            const data = await response.json();

            if (data.success) {
                this.showMessage(data.message, 'success');
                this.loadAccessHistory();
            } else {
                this.showMessage(data.message, 'error');
            }
        } catch (error) {
            console.error('删除用户失败:', error);
            this.showMessage('删除用户失败，请重试', 'error');
        }
    }

    showMessage(message, type) {
        const messageDiv = document.getElementById('add-user-message');
        messageDiv.textContent = message;
        messageDiv.className = `message ${type}`;

        // 3秒后自动隐藏消息
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'message';
        }, 3000);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// 全局应用实例
let app;

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', () => {
    app = new AccessMonitoringApp();
});

// 添加一些实用的全局函数
function showDeleteModal(userId) {
    if (app) {
        app.showDeleteModal(userId);
    }
}
