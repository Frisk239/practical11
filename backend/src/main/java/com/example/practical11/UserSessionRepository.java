package com.example.practical11;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for UserSessionEntity
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    /**
     * Find all sessions for a specific user
     */
    List<UserSessionEntity> findByUserIdOrderByLoginTimeDesc(String userId);

    /**
     * Find active sessions for a user
     */
    List<UserSessionEntity> findByUserIdAndSessionStatus(String userId, UserSessionEntity.SessionStatus status);

    /**
     * Find all active sessions
     */
    List<UserSessionEntity> findBySessionStatus(UserSessionEntity.SessionStatus status);

    /**
     * Delete all sessions for a user
     */
    void deleteByUserId(String userId);

    /**
     * Count sessions for a user
     */
    long countByUserId(String userId);

    /**
     * Find sessions within date range
     */
    @Query("SELECT s FROM UserSessionEntity s WHERE s.userId = :userId AND s.loginTime BETWEEN :startDate AND :endDate ORDER BY s.loginTime DESC")
    List<UserSessionEntity> findSessionsInDateRange(@Param("userId") String userId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
}
