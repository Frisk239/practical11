package com.example.practical11;

/**
 * MonitoringSystem class that manages access history using arrays and pointers
 * Implements dynamic memory allocation when capacity is exhausted
 * Stores system name, access list array, current size, and capacity
 */
public class MonitoringSystem {
    private String systemName;
    private AccessHistory[] accessList;
    private int currentSize;
    private int capacity;

    public MonitoringSystem(String systemName) {
        this.systemName = systemName;
        this.capacity = 5; // Default capacity for 5 unique users
        this.accessList = new AccessHistory[capacity];
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

    public int getCapacity() {
        return capacity;
    }

    /**
     * Ensures the access list has enough capacity for new elements
     * Doubles the array size when capacity is exhausted and copies existing elements
     * Time Complexity: O(n) for copying elements
     */
    private void ensureCapacity() {
        if (currentSize >= capacity) {
            int newCapacity = capacity * 2; // Double the size as required by Activity 2
            AccessHistory[] newArray = new AccessHistory[newCapacity];

            // Copy existing elements to new array - O(n)
            for (int i = 0; i < currentSize; i++) {
                newArray[i] = accessList[i];
            }

            accessList = newArray;
            capacity = newCapacity;

            System.out.println("DEBUG: Expanded access list capacity from " + (capacity/2) + " to " + capacity);
        }
    }

    /**
     * Adds a unique user to the access list if not already present
     * Maintains the access list in ascending order based on last login time
     * Time Complexity: O(n) for search + O(n log n) for sort = O(n log n)
     */
    public boolean addUser(String userId) {
        // Check if user already exists - O(n)
        for (int i = 0; i < currentSize; i++) {
            if (accessList[i].getUserId().equals(userId)) {
                // Update last login time for existing user
                accessList[i].setLastLoginTime(java.time.LocalDateTime.now());
                sortAccessList();
                return false; // User already exists, just updated time
            }
        }

        // Ensure capacity before adding - O(1) amortized, O(n) when expanding
        ensureCapacity();

        // Add new user - O(1)
        AccessHistory newHistory = new AccessHistory(userId);
        accessList[currentSize++] = newHistory;
        sortAccessList(); // O(n^2) due to bubble sort
        return true; // New user added
    }

    /**
     * Adds a user with full information to the access list
     * Time Complexity: O(n) for search + O(n log n) for sort = O(n log n)
     */
    public boolean addUser(AccessHistory user) {
        // Check if user already exists - O(n)
        for (int i = 0; i < currentSize; i++) {
            if (accessList[i].getUserId().equals(user.getUserId())) {
                return false; // User already exists
            }
        }

        // Ensure capacity before adding - O(1) amortized, O(n) when expanding
        ensureCapacity();

        // Add new user - O(1)
        accessList[currentSize++] = user;
        sortAccessList(); // O(n^2) due to bubble sort
        return true; // New user added
    }

    /**
     * Removes an existing user from the access list
     * Ensures the access list has no gaps by shifting elements
     * Time Complexity: O(n) for search + O(n) for shift = O(n)
     */
    public boolean removeUser(String userId) {
        for (int i = 0; i < currentSize; i++) {
            if (accessList[i].getUserId().equals(userId)) {
                // Shift elements to fill the gap - O(n)
                for (int j = i; j < currentSize - 1; j++) {
                    accessList[j] = accessList[j + 1];
                }
                accessList[--currentSize] = null; // Clear reference for GC
                return true;
            }
        }
        return false; // User not found
    }

    /**
     * Gets access history for a specific user
     * Time Complexity: O(n)
     */
    public AccessHistory getUserHistory(String userId) {
        for (int i = 0; i < currentSize; i++) {
            if (accessList[i].getUserId().equals(userId)) {
                return accessList[i];
            }
        }
        return null;
    }

    /**
     * Gets all access histories as a DynamicArray for compatibility
     * Time Complexity: O(n)
     */
    public DynamicArray<AccessHistory> getAllAccessHistories() {
        DynamicArray<AccessHistory> result = new DynamicArray<>();
        for (int i = 0; i < currentSize; i++) {
            result.add(accessList[i]);
        }
        return result;
    }

    /**
     * Sorts the access list in ascending order based on last login time
     * Uses bubble sort algorithm
     * Time Complexity: O(n^2)
     */
    private void sortAccessList() {
        // Simple bubble sort for the access list - O(n^2)
        for (int i = 0; i < currentSize - 1; i++) {
            for (int j = 0; j < currentSize - i - 1; j++) {
                if (accessList[j].compareTo(accessList[j + 1]) > 0) {
                    // Swap elements directly in array
                    AccessHistory temp = accessList[j];
                    accessList[j] = accessList[j + 1];
                    accessList[j + 1] = temp;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Monitoring System: ").append(systemName).append("\n");
        sb.append("Current Users: ").append(currentSize).append(" (Capacity: ").append(capacity).append(")\n");

        if (currentSize == 0) {
            sb.append("No access history available.");
        } else {
            sb.append("Access History:\n");
            for (int i = 0; i < currentSize; i++) {
                sb.append("  ").append(accessList[i].toString());
                if (i < currentSize - 1) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }
}
