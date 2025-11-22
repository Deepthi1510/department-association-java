package com.deptassoc;

import com.deptassoc.ui.ConsoleUI;

/**
 * Main entry point for the Department Association Management System.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Department Association Management System");
        System.out.println("========================================\n");
        
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
