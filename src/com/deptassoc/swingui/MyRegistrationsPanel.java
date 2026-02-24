package com.deptassoc.swingui;

import javax.swing.*;
import java.awt.*;

/**
 * Skeleton panel for students to view their registrations.
 * Shows activities that the student has registered for.
 */
public class MyRegistrationsPanel extends JPanel {
    private int studentId;
    
    public MyRegistrationsPanel(int studentId) {
        this.studentId = studentId;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Placeholder for now
        JLabel placeholder = new JLabel(
            "My Registrations for Student ID: " + studentId +
            "\n\n(Feature coming soon)",
            SwingConstants.CENTER
        );
        placeholder.setFont(new Font("Arial", Font.PLAIN, 14));
        add(placeholder, BorderLayout.CENTER);
    }
}
