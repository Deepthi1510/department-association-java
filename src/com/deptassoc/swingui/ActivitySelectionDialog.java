package com.deptassoc.swingui;

import com.deptassoc.dao.ParticipantDao;
import com.deptassoc.dto.ActivityDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal dialog for selecting and registering for activities.
 * Shows available activities that the student is not already registered for.
 */
public class ActivitySelectionDialog extends JDialog {
    private int studentId;
    private DefaultTableModel activitiesTableModel;
    private JTable activitiesTable;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel messageLabel;

    public ActivitySelectionDialog(Frame owner, int studentId) {
        super(owner, "Register for Activities", true);
        this.studentId = studentId;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title label
        JLabel titleLabel = new JLabel("Select activities to register for:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table for available activities
        activitiesTableModel = new DefaultTableModel(
            new String[]{"Activity ID", "Activity Name", "Description", "Start Time", "End Time", "Participants"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activitiesTable = new JTable(activitiesTableModel);
        activitiesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        activitiesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Message label
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        messagePanel.add(messageLabel);
        mainPanel.add(messagePanel, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> onRegisterClicked());
        buttonPanel.add(registerButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Add button panel to main panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messagePanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Shows the dialog and loads available activities.
     */
    public void showDialog() {
        loadAvailableActivities();
        setVisible(true);
    }

    /**
     * Loads activities that the student is not already registered for.
     */
    private void loadAvailableActivities() {
        new SwingWorker<List<ActivityDTO>, Void>() {
            @Override
            protected List<ActivityDTO> doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.findAvailableActivitiesForStudent(studentId);
            }

            @Override
            protected void done() {
                try {
                    List<ActivityDTO> activities = get();
                    activitiesTableModel.setRowCount(0);
                    
                    if (activities.isEmpty()) {
                        messageLabel.setText("No activities available. You are already registered for all activities.");
                        registerButton.setEnabled(false);
                    } else {
                        messageLabel.setText(" ");
                        registerButton.setEnabled(true);
                        
                        for (ActivityDTO activity : activities) {
                            activitiesTableModel.addRow(new Object[]{
                                activity.getActivityId(),
                                activity.getActivityName(),
                                activity.getDescription(),
                                activity.getStartTime(),
                                activity.getEndTime(),
                                activity.getParticipantCount()
                            });
                        }
                    }
                } catch (Exception ex) {
                    messageLabel.setText("Error loading activities: " + ex.getMessage());
                    registerButton.setEnabled(false);
                }
            }
        }.execute();
    }

    /**
     * Handles Register button click.
     * Registers the student for all selected activities.
     */
    private void onRegisterClicked() {
        int[] selectedRows = activitiesTable.getSelectedRows();
        
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one activity to register for.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Collect selected activity IDs
        List<Integer> selectedActivityIds = new ArrayList<>();
        for (int row : selectedRows) {
            int activityId = (int) activitiesTableModel.getValueAt(row, 0);
            selectedActivityIds.add(activityId);
        }

        // Register for selected activities
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                int successCount = 0;
                
                for (int activityId : selectedActivityIds) {
                    try {
                        if (dao.addParticipant(activityId, studentId)) {
                            successCount++;
                        }
                    } catch (SQLIntegrityConstraintViolationException ex) {
                        // Already registered for this activity, skip
                    } catch (SQLException ex) {
                        throw ex;
                    }
                }
                
                return successCount;
            }

            @Override
            protected void done() {
                try {
                    int successCount = get();
                    if (successCount > 0) {
                        JOptionPane.showMessageDialog(ActivitySelectionDialog.this,
                                "Successfully registered for " + successCount + " activity(ies).",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ActivitySelectionDialog.this,
                                "No activities were registered.",
                                "No Changes",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ActivitySelectionDialog.this,
                            "Error registering: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
