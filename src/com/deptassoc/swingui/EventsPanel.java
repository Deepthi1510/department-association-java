package com.deptassoc.swingui;

import com.deptassoc.dao.EventDao;
import com.deptassoc.model.Event;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays all events in a JTable using SwingWorker.
 */
public class EventsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public EventsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new DefaultTableModel(
            new String[]{"Event ID", "Name", "Description", "Date", "Venue"}, 0
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
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Loading...");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadEvents());
        
        bottomPanel.add(statusLabel);
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadEvents();
    }
    
    private void loadEvents() {
        SwingWorker<List<Event>, Void> worker = new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                statusLabel.setText("Loading events...");
                return new EventDao().findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    tableModel.setRowCount(0);
                    for (Event e : events) {
                        tableModel.addRow(new Object[]{
                            e.getEventId(),
                            e.getEventName(),
                            e.getDescription(),
                            e.getEventDate(),
                            e.getVenue()
                        });
                    }
                    statusLabel.setText("Loaded " + events.size() + " events");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        EventsPanel.this,
                        "Error loading events: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
}
