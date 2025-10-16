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
            historyMap.put("name", history.getName());
            historyMap.put("email", history.getEmail());
            historyMap.put("department", history.getDepartment());
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
        String name = request.get("name");
        String email = request.get("email");
        String department = request.get("department");

        if (userId == null || userId.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "用户ID不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "姓名不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        if (email == null || email.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "邮箱不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        // Email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.trim().matches(emailRegex)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "邮箱格式不正确");
            return ResponseEntity.badRequest().body(error);
        }

        if (department == null || department.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "部门不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        // Check if user already exists
        if (monitoringSystem.getUserHistory(userId.trim()) != null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "用户ID已存在");
            return ResponseEntity.badRequest().body(error);
        }

        AccessHistory newUser = new AccessHistory(userId.trim(), name.trim(), email.trim(), department.trim());
        boolean added = monitoringSystem.addUser(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", added);
        response.put("userId", newUser.getUserId());
        response.put("name", newUser.getName());
        response.put("email", newUser.getEmail());
        response.put("department", newUser.getDepartment());
        response.put("lastLoginTime", newUser.getLastLoginTimeISO());
        response.put("message", added ? "用户添加成功" : "用户添加失败");

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
        info.put("capacity", monitoringSystem.getCapacity()); // Dynamic capacity from MonitoringSystem
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

        userId = userId.trim();

        // Check if user exists in the monitoring system (registered users only)
        if (monitoringSystem.getUserHistory(userId) == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请联系管理员进行注册");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            UserSessionEntity session = userSessionService.startSession(userId);
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
