package com.example.practical11;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AccessHistory class represents a user's access history with user ID and last login time.
 * Implements Comparable for total ordering based on last login time.
 */
public class AccessHistory implements Comparable<AccessHistory> {
    private String userId;
    private String name;
    private String email;
    private String department;
    private LocalDateTime lastLoginTime;

    public AccessHistory(String userId) {
        this.userId = userId;
        this.lastLoginTime = LocalDateTime.now();
    }

    public AccessHistory(String userId, LocalDateTime lastLoginTime) {
        this.userId = userId;
        this.lastLoginTime = lastLoginTime;
    }

    public AccessHistory(String userId, String name, String email, String department) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.lastLoginTime = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginTimeISO() {
        return lastLoginTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        return "AccessHistory{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", lastLoginTime=" + lastLoginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                '}';
    }

    @Override
    public int compareTo(AccessHistory other) {
        return this.lastLoginTime.compareTo(other.lastLoginTime);
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
}
