package com.example.practical11;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AccessHistory class that stores user id and last login time
 * Implements Comparable for total ordering based on last login time
 */
public class AccessHistory implements Comparable<AccessHistory> {
    private String userId;
    private LocalDateTime lastLoginTime;

    public AccessHistory(String userId) {
        this.userId = userId;
        this.lastLoginTime = LocalDateTime.now();
    }

    public AccessHistory(String userId, LocalDateTime lastLoginTime) {
        this.userId = userId;
        this.lastLoginTime = lastLoginTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }

    /**
     * Returns ISO 8601 formatted date time string
     */
    public String getLastLoginTimeISO() {
        return lastLoginTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        return String.format("User: %s, Last Login: %s",
            userId,
            lastLoginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccessHistory that = (AccessHistory) obj;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public int compareTo(AccessHistory other) {
        // Ascending order based on last login time (earliest first)
        return this.lastLoginTime.compareTo(other.lastLoginTime);
    }
}
