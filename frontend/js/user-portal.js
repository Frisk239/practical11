// 用户访问门户前端逻辑
class UserPortalApp {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8080/api';
        this.currentUser = null;
        this.isOnline = false;
        this.init();
    }

    init() {
        this.bindEvents();
        this.updateUI();
    }

    bindEvents() {
        // 用户登录表单
        const loginForm = document.getElementById('login-form');
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.login();
        });

        // 访问按钮
        const accessBtn = document.getElementById('access-btn');
        accessBtn.addEventListener('click', () => {
            this.accessSystem();
        });

        // 离开按钮
        const leaveBtn = document.getElementById('leave-btn');
        leaveBtn.addEventListener('click', () => {
            this.leaveSystem();
        });
    }

    async login() {
        const usernameInput = document.getElementById('username');
        const username = usernameInput.value.trim();

        if (!username) {
            this.showMessage('请输入用户名 (Please enter username)', 'error', 'login');
            return;
        }

        try {
            // 验证用户是否已注册（通过尝试登录来验证）
            const response = await fetch(`${this.apiBaseUrl}/sessions/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userId: username })
            });

            const data = await response.json();

            if (data.success) {
                this.currentUser = username;
                this.isOnline = true; // 登录成功后自动设为在线状态
                this.updateUI();
                this.showMessage('登录成功！现在可以访问系统了。 (Login successful! You can now access the system.)', 'success', 'login');
                usernameInput.value = '';
                this.loadUserHistory();
            } else {
                this.showMessage('登录失败：' + data.message + ' (Login failed: ' + data.message + ')', 'error', 'login');
            }
        } catch (error) {
            console.error('登录失败 (Login failed):', error);
            this.showMessage('登录失败，请重试 (Login failed, please try again)', 'error', 'login');
        }
    }

    async accessSystem() {
        if (!this.currentUser) {
            this.showMessage('请先登录 (Please login first)', 'error', 'access');
            return;
        }

        if (this.isOnline) {
            this.showMessage('您已经在线了 (You are already online)', 'info', 'access');
            return;
        }

        try {
            const response = await fetch(`${this.apiBaseUrl}/sessions/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userId: this.currentUser })
            });

            const data = await response.json();

            if (data.success) {
                this.isOnline = true;
                this.updateUI();
                this.showMessage('成功访问系统！ (Successfully accessed the system!)', 'success', 'access');
                this.loadUserHistory();
            } else {
                this.showMessage('访问失败：' + data.message + ' (Access failed: ' + data.message + ')', 'error', 'access');
            }
        } catch (error) {
            console.error('访问系统失败 (Failed to access system):', error);
            this.showMessage('访问失败，请重试 (Access failed, please try again)', 'error', 'access');
        }
    }

    async leaveSystem() {
        if (!this.currentUser) {
            this.showMessage('请先登录 (Please login first)', 'error', 'access');
            return;
        }

        try {
            const response = await fetch(`${this.apiBaseUrl}/sessions/logout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userId: this.currentUser })
            });

            const data = await response.json();

            if (data.success) {
                this.isOnline = false;
                this.updateUI();
                this.showMessage('已离开系统 (Left the system)', 'info', 'access');
                this.loadUserHistory();
            } else {
                this.showMessage('离开失败：' + data.message + ' (Leave failed: ' + data.message + ')', 'error', 'access');
            }
        } catch (error) {
            console.error('离开系统失败 (Failed to leave system):', error);
            this.showMessage('离开失败，请重试 (Leave failed, please try again)', 'error', 'access');
        }
    }

    async loadUserHistory() {
        if (!this.currentUser) return;

        try {
            const response = await fetch(`${this.apiBaseUrl}/sessions/user/${this.currentUser}`);
            const userSessions = await response.json();

            this.renderUserHistory(userSessions);
        } catch (error) {
            console.error('加载用户历史失败 (Failed to load user history):', error);
        }
    }

    renderUserHistory(sessions) {
        const tbody = document.getElementById('user-history-body');

        if (sessions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="no-data">暂无访问记录 (No access records)</td></tr>';
            return;
        }

        tbody.innerHTML = sessions.map(session => {
            const loginTime = new Date(session.loginTime);
            const logoutTime = session.logoutTime ? new Date(session.logoutTime) : null;

            const formattedLoginTime = loginTime.toLocaleString('zh-CN');
            const formattedLogoutTime = logoutTime ? logoutTime.toLocaleString('zh-CN') : '-';

            return `
                <tr>
                    <td>${formattedLoginTime}</td>
                    <td>${formattedLogoutTime}</td>
                    <td>${session.duration}</td>
                </tr>
            `;
        }).join('');
    }

    updateUI() {
        const currentUserSpan = document.getElementById('current-user');
        const onlineStatusSpan = document.getElementById('online-status');
        const accessBtn = document.getElementById('access-btn');
        const leaveBtn = document.getElementById('leave-btn');

        if (this.currentUser) {
            currentUserSpan.textContent = this.currentUser;
            onlineStatusSpan.textContent = this.isOnline ? '在线 (Online)' : '离线 (Offline)';
            onlineStatusSpan.className = this.isOnline ? 'status-online' : 'status-offline';

            accessBtn.disabled = this.isOnline;
            leaveBtn.disabled = !this.isOnline;
        } else {
            currentUserSpan.textContent = '未登录 (Not logged in)';
            onlineStatusSpan.textContent = '离线 (Offline)';
            onlineStatusSpan.className = 'status-offline';

            accessBtn.disabled = true;
            leaveBtn.disabled = true;
        }
    }

    showMessage(message, type, section = 'login') {
        const messageDiv = document.getElementById(`${section}-message`);
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

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', () => {
    window.userPortalApp = new UserPortalApp();
});
