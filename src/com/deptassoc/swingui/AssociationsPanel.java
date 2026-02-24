package com.deptassoc.swingui;

import com.deptassoc.dao.AssociationDao;
import com.deptassoc.model.Association;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays all associations in a JTable using SwingWorker.
 * Uses AssociationDao to fetch data without freezing UI.
 */
public class AssociationsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public AssociationsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        tableModel = new DefaultTableModel(
            new String[]{"Assoc ID", "Name", "Description", "Est. Year"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Status and refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Loading...");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAssociations());
        
        bottomPanel.add(statusLabel);
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Load data on initialization
        loadAssociations();
    }
    
    private void loadAssociations() {
        // Use SwingWorker to load data without freezing UI
        SwingWorker<List<Association>, Void> worker = new SwingWorker<List<Association>, Void>() {
            @Override
            protected List<Association> doInBackground() throws Exception {
                statusLabel.setText("Loading associations...");
                return new AssociationDao().findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Association> associations = get();
                    populateTable(associations);
                    statusLabel.setText("Loaded " + associations.size() + " associations");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        AssociationsPanel.this,
                        "Error loading associations: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
    
    private void populateTable(List<Association> associations) {
        tableModel.setRowCount(0);
        
        for (Association assoc : associations) {
            tableModel.addRow(new Object[]{
                assoc.getAssocId(),
                assoc.getAssocName(),
                assoc.getDescription(),
                assoc.getEstablishmentYear()
            });
        }
    }
}
