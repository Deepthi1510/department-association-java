package com.deptassoc.swingui;

import com.deptassoc.ui.faculty.FacultyDashboardPanel;
import com.deptassoc.ui.association.AssociationMemberDashboardPanel;
import javax.swing.*;

/**
 * Main application frame. Displays role-specific menus based on AuthResult.
 * 
 * STUDENT → View Events, View Activities, Register for Activity
 * FACULTY → View Assigned Events, View Participants
 * ASSOCIATION_MEMBER → Manage Events, Manage Activities, View Participants
 */
public class MainFrame extends JFrame {
    private AuthResult authResult;
    private JTabbedPane tabbedPane;
    
    public MainFrame(AuthResult authResult) {
        this.authResult = authResult;
        
        setTitle("Department Association - " + authResult.getRole() + " (" + authResult.getDisplayName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create menu bar
        setJMenuBar(createMenuBar());
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add role-specific panels
        addRoleBasedPanels();
        
        add(tabbedPane);
    }
    
    /**
     * Add tabs based on user role.
     */
    private void addRoleBasedPanels() {
        String role = authResult.getRole();
        
        if ("STUDENT".equals(role)) {
            // Use the new StudentDashboardPanel for student registration features
            StudentDashboardPanel studentPanel = new StudentDashboardPanel(authResult);
            tabbedPane.addTab("Dashboard", studentPanel);
            
            // Load data when tab becomes visible
            tabbedPane.addChangeListener(e -> {
                if (tabbedPane.getSelectedComponent() == studentPanel) {
                    studentPanel.onPanelShown();
                }
            });
        } 
        else if ("FACULTY".equals(role)) {
            // Use the new FacultyDashboardPanel for faculty event and activity management
            FacultyDashboardPanel facultyPanel = new FacultyDashboardPanel(authResult);
            tabbedPane.addTab("Dashboard", facultyPanel);
            
            // Load data when tab becomes visible
            tabbedPane.addChangeListener(e -> {
                if (tabbedPane.getSelectedComponent() == facultyPanel) {
                    facultyPanel.onPanelShown();
                }
            });
        } 
        else if ("ASSOCIATION_MEMBER".equals(role)) {
            // Use the new AssociationMemberDashboardPanel with role-based view
            AssociationMemberDashboardPanel amPanel = new AssociationMemberDashboardPanel(authResult);
            tabbedPane.addTab("Dashboard", amPanel);
            
            // Load data when tab becomes visible
            tabbedPane.addChangeListener(e -> {
                if (tabbedPane.getSelectedComponent() == amPanel) {
                    amPanel.onPanelShown();
                }
            });
        }
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem userInfoItem = new JMenuItem("User Info");
        userInfoItem.addActionListener(e -> showUserInfo());
        viewMenu.add(userInfoItem);
        menuBar.add(viewMenu);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private void showUserInfo() {
        String info = String.format(
            "User Information\n\n" +
            "Display Name: %s\n" +
            "Role: %s\n" +
            "User ID: %d",
            authResult.getDisplayName(),
            authResult.getRole(),
            authResult.getUserId()
        );
        
        JOptionPane.showMessageDialog(this, info, "User Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAbout() {
        String about = "Department Association Management System\n\n" +
                      "Version 2.0 (Swing GUI - 3-Type Login)\n" +
                      "Role-based access control with JDBC backend\n\n" +
                      "© 2025";
        
        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
