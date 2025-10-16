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
            this.showMessage('请输入用户名', 'error', 'login');
            return;
        }

        try {
            // 首先尝试添加用户到系统（如果不存在）
            const response = await fetch(`${this.apiBaseUrl}/access-history`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ userId: username })
            });

            const data = await response.json();

            if (data.success) {
                this.currentUser = username;
                this.isOnline = false;
                this.updateUI();
                this.showMessage('登录成功！现在可以访问系统了。', 'success', 'login');
                usernameInput.value = '';
                this.loadUserHistory();
            } else {
                this.showMessage('登录失败：' + data.message, 'error', 'login');
            }
        } catch (error) {
            console.error('登录失败:', error);
            this.showMessage('登录失败，请重试', 'error', 'login');
        }
    }

    async accessSystem() {
        if (!this.currentUser) {
            this.showMessage('请先登录', 'error', 'access');
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
                this.showMessage('成功访问系统！', 'success', 'access');
                this.loadUserHistory();
            } else {
                this.showMessage('访问失败：' + data.message, 'error', 'access');
            }
        } catch (error) {
            console.error('访问系统失败:', error);
            this.showMessage('访问失败，请重试', 'error', 'access');
        }
    }

    async leaveSystem() {
        if (!this.currentUser) {
            this.showMessage('请先登录', 'error', 'access');
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
                this.showMessage('已离开系统', 'info', 'access');
                this.loadUserHistory();
            } else {
                this.showMessage('离开失败：' + data.message, 'error', 'access');
            }
        } catch (error) {
            console.error('离开系统失败:', error);
            this.showMessage('离开失败，请重试', 'error', 'access');
        }
    }

    async loadUserHistory() {
        if (!this.currentUser) return;

        try {
            const response = await fetch(`${this.apiBaseUrl}/sessions/user/${this.currentUser}`);
            const userSessions = await response.json();

            this.renderUserHistory(userSessions);
        } catch (error) {
            console.error('加载用户历史失败:', error);
        }
    }

    renderUserHistory(sessions) {
        const tbody = document.getElementById('user-history-body');

        if (sessions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="no-data">暂无访问记录</td></tr>';
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
            onlineStatusSpan.textContent = this.isOnline ? '在线' : '离线';
            onlineStatusSpan.className = this.isOnline ? 'status-online' : 'status-offline';

            accessBtn.disabled = this.isOnline;
            leaveBtn.disabled = !this.isOnline;
        } else {
            currentUserSpan.textContent = '未登录';
            onlineStatusSpan.textContent = '离线';
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
