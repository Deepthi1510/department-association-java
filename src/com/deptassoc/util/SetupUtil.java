package com.deptassoc.util;

import com.deptassoc.auth.AuthManager;

/**
 * Utility for one-time setup of users and password generation.
 * Run from Main or manually to create initial users.
 */
public class SetupUtil {
    
    /**
     * Generates a password hash for inclusion in users.json.
     * Usage: java com.deptassoc.util.SetupUtil hash "your_password"
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "hash":
                if (args.length < 2) {
                    System.out.println("Usage: SetupUtil hash <password>");
                    break;
                }
                generateHash(args[1]);
                break;
                
            case "init-users":
                try {
                    AuthManager.createInitialUsers();
                } catch (Exception e) {
                    System.err.println("Error creating users: " + e.getMessage());
                }
                break;
                
            case "help":
                printUsage();
                break;
                
            default:
                System.out.println("Unknown command: " + command);
                printUsage();
        }
    }
    
    /**
     * Generates and displays a password hash.
     */
    private static void generateHash(String password) {
        String hash = PasswordUtil.hash(password);
        System.out.println("\n✓ Password Hash Generated:");
        System.out.println("=======================================");
        System.out.println(hash);
        System.out.println("=======================================");
        System.out.println("\nCopy this value as 'passwordHash' in users.json");
        System.out.println("\nFormat:");
        System.out.println("{");
        System.out.println("  \"type\": \"STUDENT\",");
        System.out.println("  \"id\": 1,");
        System.out.println("  \"username\": \"username\",");
        System.out.println("  \"passwordHash\": \"" + hash + "\"");
        System.out.println("}");
    }
    
    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("\n=== Department Association Setup Utility ===");
        System.out.println("\nUsage:");
        System.out.println("  java com.deptassoc.util.SetupUtil <command> [args]");
        System.out.println("\nCommands:");
        System.out.println("  hash <password>     Generate password hash for users.json");
        System.out.println("  init-users          Create initial sample users");
        System.out.println("  help                Show this help message");
        System.out.println("\nExamples:");
        System.out.println("  java com.deptassoc.util.SetupUtil hash mypassword123");
        System.out.println("  java com.deptassoc.util.SetupUtil init-users");
    }
}
