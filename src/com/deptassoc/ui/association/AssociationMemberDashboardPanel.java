package com.deptassoc.ui.association;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.deptassoc.swingui.AuthResult;
import com.deptassoc.dao.EventDao;
import com.deptassoc.dao.ActivityDao;
import com.deptassoc.dao.AssociationMemberDao;
import com.deptassoc.dao.ParticipantDao;
import com.deptassoc.dto.ActivityDTO;
import com.deptassoc.model.Event;
import com.deptassoc.model.Activity;

public class AssociationMemberDashboardPanel extends JPanel {
    private AuthResult authResult;
    private JTabbedPane tabbedPane;
    private EventDao eventDao;
    private ActivityDao activityDao;
    private AssociationMemberDao associationMemberDao;
    private ParticipantDao participantDao;
    private JButton refreshButton;
    
    public AssociationMemberDashboardPanel(AuthResult authResult) {
        this.authResult = authResult;
        this.eventDao = new EventDao();
        this.activityDao = new ActivityDao();
        this.associationMemberDao = new AssociationMemberDao();
        this.participantDao = new ParticipantDao();
        
        setLayout(new BorderLayout());
        
        String memberRole = authResult.getMemberRole();
        
        if ("President".equals(memberRole)) {
            createPresidentDashboard();
        } else if ("Treasurer".equals(memberRole)) {
            createTreasurerDashboard();
        } else if ("Sports Secretary".equals(memberRole)) {
            createSportsDashboard();
        } else if ("Cultural Secretary".equals(memberRole)) {
            createCulturalDashboard();
        } else {
            JLabel errorLabel = new JLabel("Unknown role: " + memberRole);
            add(errorLabel, BorderLayout.CENTER);
        }
    }
    
    private void createPresidentDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Refresh button panel at top
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCurrentTab());
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        
        // Events Tab
        ScrollableTablePanel eventsPanel = new ScrollableTablePanel("Events", new String[]{"Event ID", "Name", "Description", "Date"});
        tabbedPane.addTab("Events", eventsPanel);
        
        // Activities Tab
        ScrollableTablePanel activitiesPanel = new ScrollableTablePanel("Activities", new String[]{"Activity ID", "Name", "Description"});
        tabbedPane.addTab("Activities", activitiesPanel);
        
        // Faculty Coordinators Tab
        ScrollableTablePanel facultyPanel = new ScrollableTablePanel("Faculty Coordinators", new String[]{"Faculty ID", "Name", "Department"});
        tabbedPane.addTab("Faculty Coordinators", facultyPanel);
        
        // Participants Overview Tab
        ScrollableTablePanel participantsPanel = new ScrollableTablePanel("Participants Overview", new String[]{"Activity", "Student ID", "Name", "Status"});
        tabbedPane.addTab("Participants Overview", participantsPanel);
        
        // Association Members & Roles Tab
        ScrollableTablePanel membersPanel = new ScrollableTablePanel("Members & Roles", new String[]{"Role", "Members"});
        tabbedPane.addTab("Members & Roles", membersPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createTreasurerDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Refresh button panel at top
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCurrentTab());
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        
        JPanel budgetPanel = new JPanel(new BorderLayout());
        budgetPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Treasurer Dashboard - Budget Overview"));
        budgetPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Dummy budget data
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("Total Budget Allocated: ₹500,000"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("Total Budget Spent: ₹275,000"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("Remaining Balance: ₹225,000"));
        contentPanel.add(Box.createVerticalStrut(20));
        
        String[] eventBudgetColumns = {"Event", "Allocated", "Spent", "Balance"};
        DefaultTableModel eventBudgetModel = new DefaultTableModel(eventBudgetColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        // Dummy event budget data
        eventBudgetModel.addRow(new Object[]{"Annual Meet 2024", "₹150,000", "₹100,000", "₹50,000"});
        eventBudgetModel.addRow(new Object[]{"Sports Day 2024", "₹120,000", "₹85,000", "₹35,000"});
        eventBudgetModel.addRow(new Object[]{"Cultural Fest 2024", "₹150,000", "₹90,000", "₹60,000"});
        eventBudgetModel.addRow(new Object[]{"Seminar Series", "₹100,000", "₹50,000", "₹50,000"});
        
        JTable eventBudgetTable = new JTable(eventBudgetModel);
        eventBudgetTable.setEnabled(false);
        
        contentPanel.add(new JLabel("Event-wise Budget Breakdown:"));
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(new JScrollPane(eventBudgetTable));
        
        budgetPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        
        tabbedPane.addTab("Budget Overview", budgetPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createSportsDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Refresh button panel at top
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCurrentTab());
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        
        // Sports Activities Tab
        ScrollableTablePanel sportsActivitiesPanel = new ScrollableTablePanel("Sports Activities", new String[]{"Activity ID", "Name", "Description"});
        tabbedPane.addTab("Sports Activities", sportsActivitiesPanel);
        
        // Sports Participants Tab
        ScrollableTablePanel sportsParticipantsPanel = new ScrollableTablePanel("Sports Participants", new String[]{"Activity", "Student ID", "Name", "Status"});
        tabbedPane.addTab("Sports Participants", sportsParticipantsPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createCulturalDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Refresh button panel at top
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCurrentTab());
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        
        // Cultural Activities Tab
        ScrollableTablePanel culturalActivitiesPanel = new ScrollableTablePanel("Cultural Activities", new String[]{"Activity ID", "Name", "Description"});
        tabbedPane.addTab("Cultural Activities", culturalActivitiesPanel);
        
        // Cultural Participants Tab
        ScrollableTablePanel culturalParticipantsPanel = new ScrollableTablePanel("Cultural Participants", new String[]{"Activity", "Student ID", "Name", "Status"});
        tabbedPane.addTab("Cultural Participants", culturalParticipantsPanel);
        
        // Suggestions Tab
        JPanel suggestionsPanel = new JPanel(new BorderLayout());
        suggestionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel instructionsLabel = new JLabel("Enter suggestions for cultural activities:");
        suggestionsPanel.add(instructionsLabel, BorderLayout.NORTH);
        
        JTextArea suggestionsTextArea = new JTextArea(15, 40);
        suggestionsTextArea.setLineWrap(true);
        suggestionsTextArea.setWrapStyleWord(true);
        suggestionsTextArea.setText("Type your suggestions here...");
        suggestionsTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(suggestionsTextArea);
        suggestionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel suggestionsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Suggestions");
        saveButton.addActionListener(e -> {
            String suggestions = suggestionsTextArea.getText();
            if (!suggestions.isEmpty() && !suggestions.equals("Type your suggestions here...")) {
                JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this, 
                    "Suggestions saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        suggestionsButtonPanel.add(saveButton);
        suggestionsPanel.add(suggestionsButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Suggestions", suggestionsPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public void onPanelShown() {
        if (tabbedPane != null) {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0) {
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
                if (selectedComponent instanceof ScrollableTablePanel) {
                    ((ScrollableTablePanel) selectedComponent).loadData();
                }
            }
        }
    }
    
    private void refreshCurrentTab() {
        if (tabbedPane != null) {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0) {
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
                if (selectedComponent instanceof ScrollableTablePanel) {
                    ((ScrollableTablePanel) selectedComponent).loadData();
                }
            }
        }
    }
    
    // Inner class for table panels
    private class ScrollableTablePanel extends JPanel {
        private DefaultTableModel tableModel;
        private JTable table;
        private String panelTitle;
        
        public ScrollableTablePanel(String title, String[] columnNames) {
            this.panelTitle = title;
            setLayout(new BorderLayout());
            
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            
            table = new JTable(tableModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
        }
        
        public void loadData() {
            if ("Events".equals(panelTitle)) {
                loadEventsData();
            } else if ("Activities".equals(panelTitle)) {
                loadActivitiesData();
            } else if ("Faculty Coordinators".equals(panelTitle)) {
                loadFacultyData();
            } else if ("Participants Overview".equals(panelTitle)) {
                loadParticipantsData();
            } else if ("Members & Roles".equals(panelTitle)) {
                loadMembersAndRolesData();
            } else if ("Sports Activities".equals(panelTitle)) {
                loadSportsActivitiesData();
            } else if ("Sports Participants".equals(panelTitle)) {
                loadSportsParticipantsData();
            } else if ("Cultural Activities".equals(panelTitle)) {
                loadCulturalActivitiesData();
            } else if ("Cultural Participants".equals(panelTitle)) {
                loadCulturalParticipantsData();
            }
        }
        
        public DefaultTableModel getTableModel() {
            return tableModel;
        }
        
        private void loadEventsData() {
            SwingWorker<List<Event>, Void> worker = new SwingWorker<List<Event>, Void>() {
                @Override
                protected List<Event> doInBackground() throws Exception {
                    return eventDao.findAll();
                }
                
                @Override
                protected void done() {
                    try {
                        List<Event> events = get();
                        tableModel.setRowCount(0);
                        
                        for (Event event : events) {
                            tableModel.addRow(new Object[]{
                                event.getEventId(),
                                event.getEventName(),
                                event.getDescription(),
                                event.getEventDate()
                            });
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading events: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadActivitiesData() {
            SwingWorker<List<Activity>, Void> worker = new SwingWorker<List<Activity>, Void>() {
                @Override
                protected List<Activity> doInBackground() throws Exception {
                    return activityDao.findAll();
                }
                
                @Override
                protected void done() {
                    try {
                        List<Activity> activities = get();
                        tableModel.setRowCount(0);
                        
                        for (Activity activity : activities) {
                            tableModel.addRow(new Object[]{
                                activity.getActivityId(),
                                activity.getActivityName(),
                                activity.getDescription()
                            });
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading activities: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadFacultyData() {
            SwingWorker<String[][], Void> worker = new SwingWorker<String[][], Void>() {
                @Override
                protected String[][] doInBackground() throws Exception {
                    // Dummy faculty coordinators data
                    return new String[][]{
                        {"1", "Dr. Ramesh Kumar", "Computer Science"},
                        {"2", "Dr. Priya Sharma", "Electronics"},
                        {"3", "Prof. Amit Patel", "Mechanical Engineering"},
                        {"4", "Dr. Neha Singh", "Civil Engineering"}
                    };
                }
                
                @Override
                protected void done() {
                    try {
                        String[][] data = get();
                        tableModel.setRowCount(0);
                        
                        for (String[] row : data) {
                            tableModel.addRow(row);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading faculty: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadParticipantsData() {
            SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
                @Override
                protected Object[][] doInBackground() throws Exception {
                    List<Activity> activities = activityDao.findAll();
                    List<Object[]> allParticipants = new ArrayList<>();
                    
                    for (Activity activity : activities) {
                        allParticipants.add(new Object[]{
                            activity.getActivityName(),
                            "N/A",
                            "N/A",
                            "Active"
                        });
                    }
                    
                    return allParticipants.toArray(new Object[0][]);
                }
                
                @Override
                protected void done() {
                    try {
                        Object[][] data = get();
                        tableModel.setRowCount(0);
                        
                        for (Object[] row : data) {
                            tableModel.addRow(row);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading participants: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadMembersAndRolesData() {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Map<String, List<String>> membersAndRoles = associationMemberDao.getAssociationMembersAndRoles();
                    tableModel.setRowCount(0);
                    
                    for (String role : membersAndRoles.keySet()) {
                        List<String> members = membersAndRoles.get(role);
                        tableModel.addRow(new Object[]{
                            role,
                            String.join(", ", members)
                        });
                    }
                    
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadSportsActivitiesData() {
            SwingWorker<List<Activity>, Void> worker = new SwingWorker<List<Activity>, Void>() {
                @Override
                protected List<Activity> doInBackground() throws Exception {
                    List<Activity> allActivities = activityDao.findAll();
                    List<Activity> sportsActivities = new ArrayList<>();
                    
                    for (Activity activity : allActivities) {
                        String desc = activity.getDescription();
                        if (desc != null && desc.toLowerCase().contains("sport")) {
                            sportsActivities.add(activity);
                        }
                    }
                    
                    return sportsActivities;
                }
                
                @Override
                protected void done() {
                    try {
                        List<Activity> sportsActivities = get();
                        tableModel.setRowCount(0);
                        
                        for (Activity activity : sportsActivities) {
                            tableModel.addRow(new Object[]{
                                activity.getActivityId(),
                                activity.getActivityName(),
                                activity.getDescription()
                            });
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading sports activities: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadSportsParticipantsData() {
            SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
                @Override
                protected Object[][] doInBackground() throws Exception {
                    List<Activity> allActivities = activityDao.findAll();
                    List<Object[]> sportsParticipants = new ArrayList<>();
                    
                    for (Activity activity : allActivities) {
                        String desc = activity.getDescription();
                        if (desc != null && desc.toLowerCase().contains("sport")) {
                            sportsParticipants.add(new Object[]{
                                activity.getActivityName(),
                                "N/A",
                                "N/A",
                                "Active"
                            });
                        }
                    }
                    
                    return sportsParticipants.toArray(new Object[0][]);
                }
                
                @Override
                protected void done() {
                    try {
                        Object[][] data = get();
                        tableModel.setRowCount(0);
                        
                        for (Object[] row : data) {
                            tableModel.addRow(row);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading sports participants: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadCulturalActivitiesData() {
            SwingWorker<List<Activity>, Void> worker = new SwingWorker<List<Activity>, Void>() {
                @Override
                protected List<Activity> doInBackground() throws Exception {
                    List<Activity> allActivities = activityDao.findAll();
                    List<Activity> culturalActivities = new ArrayList<>();
                    
                    for (Activity activity : allActivities) {
                        String desc = activity.getDescription();
                        if (desc != null) {
                            String lower = desc.toLowerCase();
                            if (lower.contains("cultural") || lower.contains("dance") || 
                                lower.contains("music") || lower.contains("drama")) {
                                culturalActivities.add(activity);
                            }
                        }
                    }
                    
                    return culturalActivities;
                }
                
                @Override
                protected void done() {
                    try {
                        List<Activity> culturalActivities = get();
                        tableModel.setRowCount(0);
                        
                        for (Activity activity : culturalActivities) {
                            tableModel.addRow(new Object[]{
                                activity.getActivityId(),
                                activity.getActivityName(),
                                activity.getDescription()
                            });
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading cultural activities: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void loadCulturalParticipantsData() {
            SwingWorker<Object[][], Void> worker = new SwingWorker<Object[][], Void>() {
                @Override
                protected Object[][] doInBackground() throws Exception {
                    List<Activity> allActivities = activityDao.findAll();
                    List<Object[]> culturalParticipants = new ArrayList<>();
                    
                    for (Activity activity : allActivities) {
                        String desc = activity.getDescription();
                        if (desc != null) {
                            String lower = desc.toLowerCase();
                            if (lower.contains("cultural") || lower.contains("dance") || 
                                lower.contains("music") || lower.contains("drama")) {
                                culturalParticipants.add(new Object[]{
                                    activity.getActivityName(),
                                    "N/A",
                                    "N/A",
                                    "Active"
                                });
                            }
                        }
                    }
                    
                    return culturalParticipants.toArray(new Object[0][]);
                }
                
                @Override
                protected void done() {
                    try {
                        Object[][] data = get();
                        tableModel.setRowCount(0);
                        
                        for (Object[] row : data) {
                            tableModel.addRow(row);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AssociationMemberDashboardPanel.this,
                            "Error loading cultural participants: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
