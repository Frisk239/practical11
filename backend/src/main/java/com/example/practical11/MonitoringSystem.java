package com.example.practical11;

/**
 * MonitoringSystem class that manages access history using custom DynamicArray
 * Stores system name, access list, and current size
 */
public class MonitoringSystem {
    private String systemName;
    private DynamicArray<AccessHistory> accessList;
    private int currentSize;

    public MonitoringSystem(String systemName) {
        this.systemName = systemName;
        this.accessList = new DynamicArray<>(5); // Default capacity for 5 unique users
        this.currentSize = 0;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    /**
     * Adds a unique user to the access list if not already present
     * Maintains the access list in ascending order based on last login time
     */
    public boolean addUser(String userId) {
        // Check if user already exists
        for (int i = 0; i < currentSize; i++) {
            if (accessList.get(i).getUserId().equals(userId)) {
                // Update last login time for existing user
                accessList.get(i).updateLastLoginTime();
                sortAccessList();
                return false; // User already exists, just updated time
            }
        }

        // Add new user
        AccessHistory newHistory = new AccessHistory(userId);
        accessList.add(newHistory);
        currentSize++;
        sortAccessList();
        return true; // New user added
    }

    /**
     * Removes an existing user from the access list
     * Ensures the access list has no gaps
     */
    public boolean removeUser(String userId) {
        for (int i = 0; i < currentSize; i++) {
            if (accessList.get(i).getUserId().equals(userId)) {
                accessList.remove(i);
                currentSize--;
                return true;
            }
        }
        return false; // User not found
    }

    /**
     * Gets access history for a specific user
     */
    public AccessHistory getUserHistory(String userId) {
        for (int i = 0; i < currentSize; i++) {
            if (accessList.get(i).getUserId().equals(userId)) {
                return accessList.get(i);
            }
        }
        return null;
    }

    /**
     * Gets all access histories
     */
    public DynamicArray<AccessHistory> getAllAccessHistories() {
        DynamicArray<AccessHistory> result = new DynamicArray<>();
        for (int i = 0; i < currentSize; i++) {
            result.add(accessList.get(i));
        }
        return result;
    }

    /**
     * Sorts the access list in ascending order based on last login time
     */
    private void sortAccessList() {
        // Simple bubble sort for the access list
        for (int i = 0; i < currentSize - 1; i++) {
            for (int j = 0; j < currentSize - i - 1; j++) {
                if (accessList.get(j).compareTo(accessList.get(j + 1)) > 0) {
                    // Swap
                    AccessHistory temp = accessList.get(j);
                    accessList.remove(j);
                    accessList.add(j, accessList.get(j));
                    accessList.remove(j + 1);
                    accessList.add(j + 1, temp);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Monitoring System: ").append(systemName).append("\n");
        sb.append("Current Users: ").append(currentSize).append("\n");

        if (currentSize == 0) {
            sb.append("No access history available.");
        } else {
            sb.append("Access History:\n");
            for (int i = 0; i < currentSize; i++) {
                sb.append("  ").append(accessList.get(i).toString());
                if (i < currentSize - 1) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }
}
