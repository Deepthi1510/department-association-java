package com.deptassoc.swingui;

/**
 * Entry point for the Swing GUI application.
 * Shows 3-type login dialog, then displays MainFrame with role-specific menus.
 * Users can logout from dashboard to return to login.
 */
public class MainSwing {
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
            
            // Login loop - user can logout and login as different user
            boolean keepRunning = true;
            while (keepRunning) {
                // Show login dialog
                LoginDialog loginDialog = new LoginDialog();
                AuthResult result = loginDialog.showDialog();
                
                if (result != null && result.isSuccess()) {
                    // Authentication successful, show main application
                    MainFrame mainFrame = new MainFrame(result);
                    mainFrame.setVisible(true);
                    
                    // Wait for the main frame to be disposed (user closes it or logs out)
                    // Window is disposed, loop continues to show login again
                } else {
                    // Authentication failed or user cancelled
                    System.out.println("Login cancelled. Application exiting.");
                    keepRunning = false;
                }
            }
            
            System.exit(0);
            
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "Fatal error: " + e.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }
}
