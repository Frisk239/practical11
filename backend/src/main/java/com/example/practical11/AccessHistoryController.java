package com.example.practical11;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AccessHistoryController {

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

        Map<String, Object> response = new HashMap<>();
        response.put("success", removed);
        response.put("userId", userId);
        response.put("message", removed ? "User removed successfully" : "User not found");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("systemName", monitoringSystem.getSystemName());
        info.put("currentUsers", monitoringSystem.getCurrentSize());
        info.put("capacity", 5); // Default capacity
        info.put("lastUpdated", java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return ResponseEntity.ok(info);
    }
}
