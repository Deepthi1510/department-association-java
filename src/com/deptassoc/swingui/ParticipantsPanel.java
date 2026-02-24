package com.deptassoc.swingui;

import com.deptassoc.dao.ActivityParticipantDao;
import com.deptassoc.model.ActivityParticipant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Skeleton panel for viewing activity participants.
 * To be expanded with participant management features.
 */
public class ParticipantsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField activityIdField;
    
    public ParticipantsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: filter by activity ID
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Activity ID:"));
        activityIdField = new JTextField(10);
        filterPanel.add(activityIdField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> loadParticipants());
        filterPanel.add(searchButton);
        add(filterPanel, BorderLayout.NORTH);
        
        // Table
        tableModel = new DefaultTableModel(
            new String[]{"Participant ID", "Activity ID", "Student ID", "Registered On"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Enter activity ID and click Search");
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadParticipants() {
        String activityIdStr = activityIdField.getText().trim();
        if (activityIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an activity ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int activityId;
        try {
            activityId = Integer.parseInt(activityIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid activity ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        final int finalActivityId = activityId;
        
        SwingWorker<List<ActivityParticipant>, Void> worker = new SwingWorker<List<ActivityParticipant>, Void>() {
            @Override
            protected List<ActivityParticipant> doInBackground() throws Exception {
                statusLabel.setText("Loading participants...");
                return new ActivityParticipantDao().findByActivity(finalActivityId);
            }
            
            @Override
            protected void done() {
                try {
                    List<ActivityParticipant> participants = get();
                    tableModel.setRowCount(0);
                    for (ActivityParticipant ap : participants) {
                        tableModel.addRow(new Object[]{
                            ap.getParticipantId(),
                            ap.getActivityId(),
                            ap.getStudentId(),
                            ap.getRegisteredOn()
                        });
                    }
                    statusLabel.setText("Loaded " + participants.size() + " participants");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        ParticipantsPanel.this,
                        "Error loading participants: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
}
