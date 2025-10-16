package com.example.practical11;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AccessHistoryController {

    @Autowired
    private UserSessionService userSessionService;

    private final MonitoringSystem monitoringSystem;

    public AccessHistoryController() {
        this.monitoringSystem = new MonitoringSystem("Web Access Monitoring System");
    }

    @GetMapping("/access-history")
    public ResponseEntity<List<Map<String, Object>>> getAllAccessHistory() {
        List<Map<String, Object>> result = new ArrayList<>();
        DynamicArray<AccessHistory> histories = monitoringSystem.getAllAccessHistories();

        for (int i = 0; i < histories.size(); i++) {
            AccessHistory history = histories.get(i);
            Map<String, Object> historyMap = new HashMap<>();
            historyMap.put("userId", history.getUserId());
            historyMap.put("lastLoginTime", history.getLastLoginTimeISO());
            historyMap.put("formattedTime", history.getLastLoginTime().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.add(historyMap);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/access-history")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "User ID cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        boolean isNewUser = monitoringSystem.addUser(userId.trim());
        AccessHistory history = monitoringSystem.getUserHistory(userId.trim());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isNewUser", isNewUser);
        response.put("userId", history.getUserId());
        response.put("lastLoginTime", history.getLastLoginTimeISO());
        response.put("message", isNewUser ? "User added successfully" : "User login time updated");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/access-history/{userId}")
    public ResponseEntity<Map<String, Object>> removeUser(@PathVariable String userId) {
        boolean removed = monitoringSystem.removeUser(userId);

        // 如果用户被删除，同时删除该用户的所有会话记录
        if (removed) {
            userSessionService.deleteUserSessions(userId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", removed);
        response.put("userId", userId);
        response.put("message", removed ? "User removed successfully" : "User not found");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        UserSessionService.SessionStats stats = userSessionService.getSessionStats();
        Map<String, Object> info = new HashMap<>();
        info.put("systemName", monitoringSystem.getSystemName());
        info.put("currentUsers", monitoringSystem.getCurrentSize());
        info.put("capacity", 5); // Default capacity
        info.put("totalSessions", stats.getTotalSessions());
        info.put("activeSessions", stats.getActiveSessions());
        info.put("uniqueUsers", stats.getUniqueUsers());
        info.put("lastUpdated", java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return ResponseEntity.ok(info);
    }

    // New session management endpoints

    @PostMapping("/sessions/login")
    public ResponseEntity<Map<String, Object>> userLogin(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "User ID cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            UserSessionEntity session = userSessionService.startSession(userId.trim());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", session.getUserId());
            response.put("loginTime", session.getLoginTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/sessions/logout")
    public ResponseEntity<Map<String, Object>> userLogout(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "User ID cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        boolean loggedOut = userSessionService.endSession(userId.trim());
        Map<String, Object> response = new HashMap<>();
        response.put("success", loggedOut);
        response.put("userId", userId);
        response.put("message", loggedOut ? "Logout successful" : "No active session found");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserSessions(@PathVariable String userId) {
        List<UserSessionEntity> sessions = userSessionService.getUserSessionHistory(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserSessionEntity session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("userId", session.getUserId());
            sessionMap.put("loginTime", session.getLoginTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            sessionMap.put("logoutTime", session.getLogoutTime() != null ?
                session.getLogoutTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
            sessionMap.put("status", session.getSessionStatus().toString());
            sessionMap.put("isActive", session.isActive());

            // Calculate duration if session is completed
            if (session.getLogoutTime() != null) {
                long durationMinutes = java.time.Duration.between(session.getLoginTime(), session.getLogoutTime()).toMinutes();
                sessionMap.put("duration", durationMinutes + " 分钟");
            } else {
                sessionMap.put("duration", "进行中");
            }

            result.add(sessionMap);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sessions/all")
    public ResponseEntity<List<Map<String, Object>>> getAllSessions() {
        List<UserSessionEntity> sessions = userSessionService.getAllSessions();
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserSessionEntity session : sessions) {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("userId", session.getUserId());
            sessionMap.put("loginTime", session.getLoginTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            sessionMap.put("logoutTime", session.getLogoutTime() != null ?
                session.getLogoutTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
            sessionMap.put("status", session.getSessionStatus().toString());
            sessionMap.put("isActive", session.isActive());

            // Calculate duration if session is completed
            if (session.getLogoutTime() != null) {
                long durationMinutes = java.time.Duration.between(session.getLoginTime(), session.getLogoutTime()).toMinutes();
                sessionMap.put("duration", durationMinutes + " 分钟");
            } else {
                sessionMap.put("duration", "进行中");
            }

            result.add(sessionMap);
        }

        return ResponseEntity.ok(result);
    }

    @Override
    public void finalize() {
        // This will be called when the application shuts down
        userSessionService.endAllActiveSessions();
    }
}
