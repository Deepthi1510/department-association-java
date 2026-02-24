package com.deptassoc.swingui;

import javax.swing.*;
import java.awt.*;

/**
 * Skeleton panel for registration approval by faculty.
 * Will display pending registrations and approval options.
 */
public class RegistrationApprovalPanel extends JPanel {
    
    public RegistrationApprovalPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Placeholder
        JLabel placeholder = new JLabel(
            "Registration Approval Panel\n\n(Feature coming soon)",
            SwingConstants.CENTER
        );
        placeholder.setFont(new Font("Arial", Font.PLAIN, 14));
        add(placeholder, BorderLayout.CENTER);
    }
}
