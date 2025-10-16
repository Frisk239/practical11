package com.example.practical11;

/**
 * UserActivity class containing main function that drives events
 * Creates a monitoring system instance and demonstrates add, remove, and print functionality
 */
public class UserActivity {

    public static void main(String[] args) {
        System.out.println("=== Access Monitoring System Demo ===\n");

        // Create monitoring system instance
        MonitoringSystem monitoringSystem = new MonitoringSystem("Enterprise Security System");

        // Demonstrate adding users
        System.out.println("1. Adding users to the system:");
        monitoringSystem.addUser("alice");
        monitoringSystem.addUser("bob");
        monitoringSystem.addUser("charlie");
        System.out.println("Added users: alice, bob, charlie\n");

        // Print current state
        System.out.println("Current system state:");
        System.out.println(monitoringSystem);
        System.out.println();

        // Demonstrate adding existing user (should update login time)
        System.out.println("2. Adding existing user 'alice' (should update login time):");
        monitoringSystem.addUser("alice");
        System.out.println("Updated alice's login time\n");

        // Print updated state
        System.out.println("Updated system state:");
        System.out.println(monitoringSystem);
        System.out.println();

        // Demonstrate removing a user
        System.out.println("3. Removing user 'bob':");
        boolean removed = monitoringSystem.removeUser("bob");
        System.out.println("User 'bob' removed: " + removed + "\n");

        // Print state after removal
        System.out.println("System state after removal:");
        System.out.println(monitoringSystem);
        System.out.println();

        // Demonstrate adding more users
        System.out.println("4. Adding more users:");
        monitoringSystem.addUser("diana");
        monitoringSystem.addUser("eve");
        monitoringSystem.addUser("frank");
        System.out.println("Added users: diana, eve, frank\n");

        // Print final state
        System.out.println("Final system state:");
        System.out.println(monitoringSystem);
        System.out.println();

        // Demonstrate trying to remove non-existent user
        System.out.println("5. Trying to remove non-existent user 'xyz':");
        removed = monitoringSystem.removeUser("xyz");
        System.out.println("User 'xyz' removed: " + removed + "\n");

        System.out.println("=== Demo completed ===");
    }
}
