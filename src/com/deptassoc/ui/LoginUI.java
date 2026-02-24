package com.deptassoc.ui;

import com.deptassoc.auth.AuthManager;
import com.deptassoc.auth.AuthResult;
import java.util.Scanner;

/**
 * Console-based login UI.
 * Prompts user for credentials and authenticates against AuthManager.
 */
public class LoginUI {
    
    private Scanner scanner;
    private static final int MAX_ATTEMPTS = 5;
    
    public LoginUI() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Displays login prompt and returns AuthResult.
     */
    public AuthResult promptLogin() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("   Department Association System");
        System.out.println("════════════════════════════════════════");
        
        if (!AuthManager.usersFileExists()) {
            System.out.println("\n❌ No users configured.");
            System.out.println("Please run: AuthManager.createInitialUsers()");
            System.out.println("Or create users.json manually.\n");
            return null;
        }
        
        System.out.println("\n👤 Please Login\n");
        
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            String username = readString("Username: ");
            String password = readPassword("Password: ");
            
            AuthResult result = AuthManager.authenticate(username, password);
            
            if (result.isSuccess()) {
                System.out.println("\n✓ Login successful!");
                System.out.println("Welcome, " + result.getDisplayName() + " (" + result.getRole() + ")");
                return result;
            } else {
                attempts++;
                int remaining = MAX_ATTEMPTS - attempts;
                if (remaining > 0) {
                    System.out.println("\n❌ Invalid credentials. (" + remaining + " attempts remaining)\n");
                } else {
                    System.out.println("\n❌ Maximum login attempts exceeded. Exiting.\n");
                    return null;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Reads a string from console input.
     */
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Reads password without echoing to console (fallback to regular input if System.console() unavailable).
     */
    private String readPassword(String prompt) {
        System.out.print(prompt);
        
        // Try to use System.console() for password masking
        java.io.Console console = System.console();
        if (console != null) {
            char[] pwd = console.readPassword();
            return new String(pwd);
        } else {
            // Fallback to regular input (e.g., in IDEs)
            return scanner.nextLine().trim();
        }
    }
}
