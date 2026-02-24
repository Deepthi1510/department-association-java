package com.deptassoc.swingui;

import com.deptassoc.dao.StudentDao;
import com.deptassoc.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays all students in a JTable using SwingWorker.
 */
public class StudentsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public StudentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new DefaultTableModel(
            new String[]{"Student ID", "Name", "Email", "Phone"}, 0
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
        refreshButton.addActionListener(e -> loadStudents());
        
        bottomPanel.add(statusLabel);
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadStudents();
    }
    
    private void loadStudents() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                statusLabel.setText("Loading students...");
                return new StudentDao().findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    tableModel.setRowCount(0);
                    for (Student s : students) {
                        tableModel.addRow(new Object[]{
                            s.getStudentId(), s.getSName(), s.getSEmail(), s.getPhone()
                        });
                    }
                    statusLabel.setText("Loaded " + students.size() + " students");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        StudentsPanel.this,
                        "Error loading students: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
}
