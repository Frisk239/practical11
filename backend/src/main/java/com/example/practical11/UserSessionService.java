package com.example.practical11;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing user sessions with database persistence
 */
@Service
@Transactional
public class UserSessionService {

    @Autowired
    private UserSessionRepository sessionRepository;

    /**
     * Start a new user session (login)
     */
    public UserSessionEntity startSession(String userId) {
        // Check if user already has an active session
        List<UserSessionEntity> activeSessions = sessionRepository
            .findByUserIdAndSessionStatus(userId, UserSessionEntity.SessionStatus.ACTIVE);

        if (!activeSessions.isEmpty()) {
            // Return existing active session
            return activeSessions.get(0);
        }

        // Create new session
        UserSessionEntity newSession = new UserSessionEntity(userId, LocalDateTime.now());
        return sessionRepository.save(newSession);
    }

    /**
     * End user session (logout)
     */
    public boolean endSession(String userId) {
        List<UserSessionEntity> activeSessions = sessionRepository
            .findByUserIdAndSessionStatus(userId, UserSessionEntity.SessionStatus.ACTIVE);

        if (activeSessions.isEmpty()) {
            return false; // No active session found
        }

        UserSessionEntity session = activeSessions.get(0);
        session.setLogoutTime(LocalDateTime.now());
        sessionRepository.save(session);
        return true;
    }

    /**
     * Get user's session history
     */
    public List<UserSessionEntity> getUserSessionHistory(String userId) {
        return sessionRepository.findByUserIdOrderByLoginTimeDesc(userId);
    }

    /**
     * Get all sessions (for admin)
     */
    public List<UserSessionEntity> getAllSessions() {
        return sessionRepository.findAll().stream()
            .sorted((a, b) -> b.getLoginTime().compareTo(a.getLoginTime()))
            .collect(Collectors.toList());
    }

    /**
     * Check if user has active session
     */
    public boolean hasActiveSession(String userId) {
        return !sessionRepository
            .findByUserIdAndSessionStatus(userId, UserSessionEntity.SessionStatus.ACTIVE)
            .isEmpty();
    }

    /**
     * Get active sessions count
     */
    public long getActiveSessionsCount() {
        return sessionRepository.findBySessionStatus(UserSessionEntity.SessionStatus.ACTIVE).size();
    }

    /**
     * Delete all sessions for a user (when user is deleted)
     */
    public void deleteUserSessions(String userId) {
        sessionRepository.deleteByUserId(userId);
    }

    /**
     * End all active sessions (application shutdown)
     */
    @PreDestroy
    public void endAllActiveSessions() {
        List<UserSessionEntity> activeSessions = sessionRepository
            .findBySessionStatus(UserSessionEntity.SessionStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        for (UserSessionEntity session : activeSessions) {
            session.setLogoutTime(now);
            sessionRepository.save(session);
        }
    }

    /**
     * Get session statistics
     */
    public SessionStats getSessionStats() {
        List<UserSessionEntity> allSessions = sessionRepository.findAll();
        List<UserSessionEntity> activeSessions = sessionRepository
            .findBySessionStatus(UserSessionEntity.SessionStatus.ACTIVE);

        return new SessionStats(
            allSessions.size(),
            activeSessions.size(),
            allSessions.stream().mapToLong(s -> s.getUserId().hashCode()).distinct().count()
        );
    }

    /**
     * Session statistics DTO
     */
    public static class SessionStats {
        private final long totalSessions;
        private final long activeSessions;
        private final long uniqueUsers;

        public SessionStats(long totalSessions, long activeSessions, long uniqueUsers) {
            this.totalSessions = totalSessions;
            this.activeSessions = activeSessions;
            this.uniqueUsers = uniqueUsers;
        }

        public long getTotalSessions() { return totalSessions; }
        public long getActiveSessions() { return activeSessions; }
        public long getUniqueUsers() { return uniqueUsers; }
    }
}
