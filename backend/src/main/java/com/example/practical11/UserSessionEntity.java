package com.example.practical11;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for User Sessions - represents database table for user access sessions
 */
@Entity
@Table(name = "user_sessions")
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "session_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;

    public enum SessionStatus {
        ACTIVE, COMPLETED
    }

    public UserSessionEntity() {}

    public UserSessionEntity(String userId, LocalDateTime loginTime) {
        this.userId = userId;
        this.loginTime = loginTime;
        this.sessionStatus = SessionStatus.ACTIVE;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
        this.sessionStatus = SessionStatus.COMPLETED;
    }

    public SessionStatus getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(SessionStatus sessionStatus) { this.sessionStatus = sessionStatus; }

    public boolean isActive() {
        return sessionStatus == SessionStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "UserSessionEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                ", sessionStatus=" + sessionStatus +
                '}';
    }
}
