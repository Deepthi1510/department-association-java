package com.deptassoc;

import com.deptassoc.ui.ConsoleUI;
import com.deptassoc.ui.LoginUI;
import com.deptassoc.auth.AuthManager;
import com.deptassoc.auth.AuthResult;

/**
 * CHANGED: Main entry point for the Department Association Management System.
 * Now includes role-based authentication before showing menu.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Initialize authentication system
            AuthManager.init();
            
            // Show login screen
            LoginUI loginUI = new LoginUI();
            AuthResult authResult = loginUI.promptLogin();
            
            if (authResult == null) {
                System.out.println("\n❌ Login failed. Exiting.");
                System.exit(1);
            }
            
            // Show role-specific menu
            System.out.println("\n========================================");
            System.out.println("Department Association Management System");
            System.out.println("========================================\n");
            
            ConsoleUI ui = new ConsoleUI(authResult);
            ui.start();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

