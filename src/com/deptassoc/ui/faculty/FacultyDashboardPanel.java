package com.deptassoc.ui.faculty;

import com.deptassoc.dao.FacultyDao;
import com.deptassoc.dto.ActivityDTO;
import com.deptassoc.dto.EventDTO;
import com.deptassoc.dto.StudentDTO;
import com.deptassoc.swingui.AuthResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Faculty dashboard with three tabs: My Events, Participants, and Manage Activities.
 * Provides faculty with event management and participant viewing capabilities.
 */
public class FacultyDashboardPanel extends JPanel {
    private AuthResult authResult;
    private JTabbedPane tabbedPane;
    private int facultyId;

    // My Events tab
    private DefaultTableModel eventsTableModel;
    private JTable eventsTable;
    private JButton refreshEventsButton;

    // Participants tab
    private JComboBox<EventDTO> eventComboBox;
    private JComboBox<ActivityDTO> activityComboBox;
    private JLabel participantCountLabel;
    private DefaultTableModel participantsTableModel;
    private JTable participantsTable;
    private JButton refreshParticipantsButton;

    // Manage Activities tab
    private JComboBox<EventDTO> manageEventComboBox;
    private DefaultTableModel activitiesTableModel;
    private JTable activitiesTable;
    private JButton addActivityButton;
    private JButton deleteActivityButton;
    private JButton refreshActivitiesButton;

    public FacultyDashboardPanel(AuthResult authResult) {
        this.authResult = authResult;
        this.facultyId = authResult.getUserId();
        setLayout(new BorderLayout());

        // Top panel with logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Faculty Dashboard - " + authResult.getDisplayName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        add(topPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Events", createMyEventsPanel());
        tabbedPane.addTab("Participants", createParticipantsPanel());
        tabbedPane.addTab("Manage Activities", createManageActivitiesPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadMyEvents();
    }
    
    /**
     * Handle logout action.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Get the parent window and close it
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.dispose();
            }
        }
    }

    /**
     * Creates the My Events tab.
     */
    private JPanel createMyEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header label
        JLabel headerLabel = new JLabel("Your Assigned Events");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(headerLabel, BorderLayout.NORTH);

        eventsTableModel = new DefaultTableModel(
            new String[]{"Event ID", "Event Name", "Date", "Venue", "Description"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventsTable = new JTable(eventsTableModel);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        refreshEventsButton = new JButton("Refresh");
        refreshEventsButton.addActionListener(e -> loadMyEvents());
        buttonPanel.add(refreshEventsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the Participants tab with clear instructions.
     */
    private JPanel createParticipantsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header section
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel headerLabel = new JLabel("View Students Registered for Activities");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Filter panel with clear labels and instructions
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Select Event → Step 2: Select Activity → View Participants Below"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Select Event:"));
        eventComboBox = new JComboBox<>();
        eventComboBox.addActionListener(e -> onEventSelected());
        row1.add(eventComboBox);
        filterPanel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Select Activity:"));
        activityComboBox = new JComboBox<>();
        activityComboBox.addActionListener(e -> onActivitySelected());
        row2.add(activityComboBox);
        filterPanel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        participantCountLabel = new JLabel("Participants: 0");
        participantCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        row3.add(participantCountLabel);
        row3.add(new JSeparator(JSeparator.VERTICAL));
        refreshParticipantsButton = new JButton("Refresh");
        refreshParticipantsButton.addActionListener(e -> loadEvents());
        row3.add(refreshParticipantsButton);
        filterPanel.add(row3);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table with header
        JPanel tablePanel = new JPanel(new BorderLayout(0, 5));
        JLabel tableLabel = new JLabel("Registered Students:");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 12));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        participantsTableModel = new DefaultTableModel(
            new String[]{"Student ID", "Name", "Email", "Phone", "Registered On"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        participantsTable = new JTable(participantsTableModel);
        participantsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(participantsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the Manage Activities tab.
     */
    private JPanel createManageActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header section
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel headerLabel = new JLabel("Create and Manage Activities for Your Events");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Event selector with clear instruction
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Select an Event to Manage its Activities"));

        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        selectorRow.add(new JLabel("Event:"));
        manageEventComboBox = new JComboBox<>();
        manageEventComboBox.addActionListener(e -> onManageEventSelected());
        selectorRow.add(manageEventComboBox);
        filterPanel.add(selectorRow);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Activities table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 5));
        JLabel tableLabel = new JLabel("Activities for Selected Event:");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 12));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

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
        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activitiesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addActivityButton = new JButton("+ Add Activity");
        deleteActivityButton = new JButton("- Delete Activity");
        refreshActivitiesButton = new JButton("Refresh");

        addActivityButton.addActionListener(e -> onAddActivityClicked());
        deleteActivityButton.addActionListener(e -> onDeleteActivityClicked());
        refreshActivitiesButton.addActionListener(e -> loadMyEvents());

        buttonPanel.add(addActivityButton);
        buttonPanel.add(deleteActivityButton);
        buttonPanel.add(refreshActivitiesButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Load all events for the faculty.
     */
    private void loadMyEvents() {
        new SwingWorker<List<EventDTO>, Void>() {
            @Override
            protected List<EventDTO> doInBackground() throws Exception {
                FacultyDao dao = new FacultyDao();
                return dao.getEventsForFaculty(facultyId);
            }

            @Override
            protected void done() {
                try {
                    List<EventDTO> events = get();
                    
                    // Update My Events tab
                    eventsTableModel.setRowCount(0);
                    for (EventDTO event : events) {
                        eventsTableModel.addRow(new Object[]{
                            event.getEventId(),
                            event.getEventName(),
                            event.getEventDate(),
                            event.getVenue(),
                            event.getDescription()
                        });
                    }

                    // Update Participants tab event dropdown
                    Object selectedEventParticipants = eventComboBox.getSelectedItem();
                    eventComboBox.removeAllItems();
                    for (EventDTO event : events) {
                        eventComboBox.addItem(event);
                    }
                    if (!events.isEmpty() && selectedEventParticipants == null) {
                        eventComboBox.setSelectedIndex(0);
                    }

                    // Update Manage Activities tab event dropdown
                    Object selectedEventManage = manageEventComboBox.getSelectedItem();
                    manageEventComboBox.removeAllItems();
                    for (EventDTO event : events) {
                        manageEventComboBox.addItem(event);
                    }
                    if (!events.isEmpty() && selectedEventManage == null) {
                        manageEventComboBox.setSelectedIndex(0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                            "Error loading events: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Load events to populate dropdowns.
     */
    private void loadEvents() {
        loadMyEvents();
    }

    /**
     * Handle event selection in Participants tab.
     */
    private void onEventSelected() {
        EventDTO selectedEvent = (EventDTO) eventComboBox.getSelectedItem();
        if (selectedEvent == null) {
            activityComboBox.removeAllItems();
            participantsTableModel.setRowCount(0);
            participantCountLabel.setText("Participants: 0");
            return;
        }

        new SwingWorker<List<ActivityDTO>, Void>() {
            @Override
            protected List<ActivityDTO> doInBackground() throws Exception {
                FacultyDao dao = new FacultyDao();
                return dao.getActivitiesForEvent(selectedEvent.getEventId());
            }

            @Override
            protected void done() {
                try {
                    List<ActivityDTO> activities = get();
                    activityComboBox.removeAllItems();
                    for (ActivityDTO activity : activities) {
                        activityComboBox.addItem(activity);
                    }
                    
                    // Auto-select first activity if available
                    if (!activities.isEmpty()) {
                        activityComboBox.setSelectedIndex(0);
                    } else {
                        participantsTableModel.setRowCount(0);
                        participantCountLabel.setText("Participants: 0");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                            "Error loading activities: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handle activity selection in Participants tab.
     */
    private void onActivitySelected() {
        ActivityDTO selectedActivity = (ActivityDTO) activityComboBox.getSelectedItem();
        if (selectedActivity == null) {
            participantsTableModel.setRowCount(0);
            participantCountLabel.setText("Participants: 0");
            return;
        }

        new SwingWorker<List<StudentDTO>, Void>() {
            @Override
            protected List<StudentDTO> doInBackground() throws Exception {
                FacultyDao dao = new FacultyDao();
                return dao.getParticipantsForActivity(selectedActivity.getActivityId());
            }

            @Override
            protected void done() {
                try {
                    List<StudentDTO> students = get();
                    participantsTableModel.setRowCount(0);
                    for (StudentDTO student : students) {
                        participantsTableModel.addRow(new Object[]{
                            student.getStudentId(),
                            student.getName(),
                            student.getEmail(),
                            student.getPhone(),
                            student.getRegisteredOn()
                        });
                    }
                    participantCountLabel.setText("Participants: " + students.size());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                            "Error loading participants: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handle event selection in Manage Activities tab.
     */
    private void onManageEventSelected() {
        EventDTO selectedEvent = (EventDTO) manageEventComboBox.getSelectedItem();
        if (selectedEvent == null) {
            return;
        }

        new SwingWorker<List<ActivityDTO>, Void>() {
            @Override
            protected List<ActivityDTO> doInBackground() throws Exception {
                FacultyDao dao = new FacultyDao();
                return dao.getActivitiesForEvent(selectedEvent.getEventId());
            }

            @Override
            protected void done() {
                try {
                    List<ActivityDTO> activities = get();
                    activitiesTableModel.setRowCount(0);
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
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                            "Error loading activities: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handle Add Activity button click.
     */
    private void onAddActivityClicked() {
        EventDTO selectedEvent = (EventDTO) manageEventComboBox.getSelectedItem();
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddActivityDialog dialog = new AddActivityDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            selectedEvent.getEventId()
        );
        dialog.setVisible(true);

        ActivityDTO createdActivity = dialog.getCreatedActivity();
        if (createdActivity != null) {
            onManageEventSelected(); // Refresh
        }
    }

    /**
     * Handle Delete Activity button click.
     */
    private void onDeleteActivityClicked() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an activity.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int activityId = (int) activitiesTableModel.getValueAt(selectedRow, 0);
        String activityName = (String) activitiesTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete activity: " + activityName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                FacultyDao dao = new FacultyDao();
                return dao.deleteActivity(activityId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                                "Activity deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        onManageEventSelected(); // Refresh
                    }
                } catch (Exception ex) {
                    if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                                "Cannot delete — participants already registered.",
                                "Delete Failed",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(FacultyDashboardPanel.this,
                                "Error deleting activity: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    /**
     * Call this method when the panel becomes visible to load initial data.
     */
    public void onPanelShown() {
        loadMyEvents();
    }
}
