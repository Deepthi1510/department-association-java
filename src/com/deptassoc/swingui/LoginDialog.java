package com.deptassoc.swingui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CountDownLatch;

/**
 * 3-Type Login Dialog.
 * Allows selection of login type: Student, Faculty, Association Member.
 * Uses database fields for authentication (name + email matching).
 */
public class LoginDialog extends JDialog {
    private JComboBox<String> loginTypeCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private AuthResult result;
    private CountDownLatch dialogClosedLatch;

    public LoginDialog() {
        super((JFrame) null, "Department Association - Login", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Department Association");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 10));

        // Login type selector
        formPanel.add(new JLabel("Login Type:"));
        loginTypeCombo = new JComboBox<>(new String[]{"Student", "Faculty", "Association Member"});
        loginTypeCombo.setSelectedIndex(0);
        loginTypeCombo.addActionListener(e -> updateFieldLabels());
        formPanel.add(loginTypeCombo);

        // Username field
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        // Password field
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        formPanel.add(passwordField);

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formPanel.add(messageLabel);
        formPanel.add(new JLabel(""));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        buttonPanel.add(loginButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> handleCancel());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Shows the login dialog and blocks (on calling thread) until user logs in or cancels.
     * Returns AuthResult if login successful, null if cancelled or closed.
     * This method blocks until the dialog is disposed.
     * 
     * @return AuthResult if authentication succeeded, null if cancelled/closed
     */
    public AuthResult showDialog() {
        result = null;
        dialogClosedLatch = new CountDownLatch(1);
        
        // Show dialog on EDT
        SwingUtilities.invokeLater(() -> setVisible(true));
        
        // Block caller until dialog closes
        try {
            dialogClosedLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return result;
    }

    /**
     * Handle Cancel button: sets result to null and closes dialog.
     */
    private void handleCancel() {
        result = null;
        closeDialog();
    }

    /**
     * Closes the dialog and signals the waiting thread.
     */
    private void closeDialog() {
        dispose();
        if (dialogClosedLatch != null) {
            dialogClosedLatch.countDown();
        }
    }

    /**
     * Updates field labels based on selected login type.
     */
    private void updateFieldLabels() {
        String selected = (String) loginTypeCombo.getSelectedItem();
        if ("Faculty".equals(selected)) {
            // Field labels stay the same (Name, Email)
        } else {
            // Student and Association Member use the same fields
        }
    }

    /**
     * Performs login authentication on a background thread.
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        String loginType = (String) loginTypeCombo.getSelectedItem();
        String loginTypeKey = mapLoginType(loginType);

        // Show loading message
        messageLabel.setText("Authenticating...");
        loginButton.setEnabled(false);

        // Perform authentication on background thread
        SwingWorker<AuthResult, Void> worker = new SwingWorker<AuthResult, Void>() {
            @Override
            protected AuthResult doInBackground() throws Exception {
                return AuthService.authenticate(loginTypeKey, username, password);
            }

            @Override
            protected void done() {
                try {
                    result = get();

                    if (result.isSuccess()) {
                        // Close dialog on successful login (on EDT)
                        SwingUtilities.invokeLater(() -> closeDialog());
                    } else {
                        messageLabel.setText("Invalid credentials. Please try again.");
                        passwordField.setText("");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    messageLabel.setText("Error: " + e.getMessage());
                    loginButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    /**
     * Maps UI login type to internal key.
     */
    private String mapLoginType(String uiType) {
        switch (uiType) {
            case "Student":
                return "STUDENT";
            case "Faculty":
                return "FACULTY";
            case "Association Member":
                return "ASSOCIATION_MEMBER";
            default:
                return "STUDENT";
        }
    }
}
