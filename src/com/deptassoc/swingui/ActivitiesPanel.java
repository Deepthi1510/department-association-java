package com.deptassoc.swingui;

import com.deptassoc.dao.ActivityDao;
import com.deptassoc.model.Activity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays activities for selected event in a JTable.
 * Uses ActivityDao with SwingWorker.
 */
public class ActivitiesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField eventIdField;
    
    public ActivitiesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: filter by event ID
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Event ID:"));
        eventIdField = new JTextField(10);
        filterPanel.add(eventIdField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> loadActivitiesByEvent());
        filterPanel.add(searchButton);
        add(filterPanel, BorderLayout.NORTH);
        
        // Table
        tableModel = new DefaultTableModel(
            new String[]{"Activity ID", "Name", "Description", "Type"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Enter event ID and click Search");
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadActivitiesByEvent() {
        String eventIdStr = eventIdField.getText().trim();
        if (eventIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an event ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int eventId;
        try {
            eventId = Integer.parseInt(eventIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid event ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        final int finalEventId = eventId;
        
        SwingWorker<List<Activity>, Void> worker = new SwingWorker<List<Activity>, Void>() {
            @Override
            protected List<Activity> doInBackground() throws Exception {
                statusLabel.setText("Loading activities...");
                return new ActivityDao().findByEvent(finalEventId);
            }
            
            @Override
            protected void done() {
                try {
                    List<Activity> activities = get();
                    tableModel.setRowCount(0);
                    for (Activity a : activities) {
                        tableModel.addRow(new Object[]{
                            a.getActivityId(),
                            a.getActivityName(),
                            a.getDescription(),
                            "Activity"
                        });
                    }
                    statusLabel.setText("Loaded " + activities.size() + " activities");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        ActivitiesPanel.this,
                        "Error loading activities: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
}
