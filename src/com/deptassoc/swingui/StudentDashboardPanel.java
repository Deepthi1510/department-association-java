package com.deptassoc.swingui;

import com.deptassoc.dao.EventDao;
import com.deptassoc.dao.ParticipantDao;
import com.deptassoc.dto.ActivityDTO;
import com.deptassoc.dto.RegistrationDTO;
import com.deptassoc.model.Event;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Student dashboard with three tabs: Events, Activities, and My Registrations.
 * Provides a comprehensive interface for students to browse and register for activities.
 */
public class StudentDashboardPanel extends JPanel {
    private AuthResult authResult;
    private JTabbedPane tabbedPane;
    
    // Events tab
    private JTable eventsTable;
    private DefaultTableModel eventsTableModel;
    private JButton viewActivitiesButton;
    private JButton refreshEventsButton;
    private Event selectedEvent;
    
    // Activities tab
    private JTable activitiesTable;
    private DefaultTableModel activitiesTableModel;
    private JButton registerButton;
    private JButton refreshActivitiesButton;
    
    // My Registrations tab
    private JTable registrationsTable;
    private DefaultTableModel registrationsTableModel;
    private JButton registerNewButton;
    private JButton editButton;
    private JButton cancelButton;
    private JButton refreshRegistrationsButton;

    public StudentDashboardPanel(AuthResult authResult) {
        this.authResult = authResult;
        setLayout(new BorderLayout());
        
        // Top panel with logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Student Dashboard - " + authResult.getDisplayName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        add(topPanel, BorderLayout.NORTH);
        
        // Tabbed pane with content
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Events", createEventsPanel());
        tabbedPane.addTab("Activities", createActivitiesPanel());
        tabbedPane.addTab("My Registrations", createMyRegistrationsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Load initial data
        loadEvents();
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
     * Creates the Events tab UI.
     */
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        eventsTableModel = new DefaultTableModel(
            new String[]{"Event ID", "Event Name", "Description", "Date", "Venue"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventsTable = new JTable(eventsTableModel);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        viewActivitiesButton = new JButton("View Activities");
        refreshEventsButton = new JButton("Refresh");

        viewActivitiesButton.addActionListener(e -> onViewActivitiesClicked());
        refreshEventsButton.addActionListener(e -> loadEvents());

        buttonPanel.add(viewActivitiesButton);
        buttonPanel.add(refreshEventsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the Activities tab UI.
     */
    private JPanel createActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
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
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        registerButton = new JButton("Register");
        refreshActivitiesButton = new JButton("Refresh");

        registerButton.addActionListener(e -> onRegisterClicked());
        refreshActivitiesButton.addActionListener(e -> loadAllActivities());

        buttonPanel.add(registerButton);
        buttonPanel.add(refreshActivitiesButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the My Registrations tab UI.
     */
    private JPanel createMyRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        registrationsTableModel = new DefaultTableModel(
            new String[]{"Participant ID", "Activity ID", "Activity Name", "Event Name", "Registered On"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registrationsTable = new JTable(registrationsTableModel);
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(registrationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        registerNewButton = new JButton("Register");
        editButton = new JButton("Edit");
        cancelButton = new JButton("Cancel Registration");
        refreshRegistrationsButton = new JButton("Refresh");

        registerNewButton.addActionListener(e -> onRegisterNewClicked());
        editButton.addActionListener(e -> onEditClicked());
        cancelButton.addActionListener(e -> onCancelClicked());
        refreshRegistrationsButton.addActionListener(e -> loadMyRegistrations());

        buttonPanel.add(registerNewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshRegistrationsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads all events from the database.
     */
    private void loadEvents() {
        new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                EventDao eventDao = new EventDao();
                return eventDao.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    eventsTableModel.setRowCount(0);
                    
                    for (Event event : events) {
                        eventsTableModel.addRow(new Object[]{
                            event.getEventId(),
                            event.getEventName(),
                            event.getDescription(),
                            event.getEventDate(),
                            event.getVenue()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error loading events: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handles "View Activities" button click.
     * Filters activities by selected event.
     */
    private void onViewActivitiesClicked() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventsTableModel.getValueAt(selectedRow, 0);
        loadActivitiesByEvent(eventId);
        tabbedPane.setSelectedIndex(1); // Switch to Activities tab
    }

    /**
     * Loads activities for a specific event.
     */
    private void loadActivitiesByEvent(int eventId) {
        new SwingWorker<List<ActivityDTO>, Void>() {
            @Override
            protected List<ActivityDTO> doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.findActivitiesByEvent(eventId);
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
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error loading activities: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Loads all activities from the database.
     */
    private void loadAllActivities() {
        new SwingWorker<List<ActivityDTO>, Void>() {
            @Override
            protected List<ActivityDTO> doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.findAllActivities();
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
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error loading activities: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Loads all registrations for the logged-in student.
     */
    private void loadMyRegistrations() {
        new SwingWorker<List<RegistrationDTO>, Void>() {
            @Override
            protected List<RegistrationDTO> doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.findRegistrationsByStudent(authResult.getUserId());
            }

            @Override
            protected void done() {
                try {
                    List<RegistrationDTO> registrations = get();
                    registrationsTableModel.setRowCount(0);
                    
                    for (RegistrationDTO reg : registrations) {
                        registrationsTableModel.addRow(new Object[]{
                            reg.getParticipantId(),
                            reg.getActivityId(),
                            reg.getActivityName(),
                            reg.getEventName(),
                            reg.getRegisteredOn()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error loading registrations: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handles Register button click.
     */
    private void onRegisterClicked() {
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
                "Register for activity: " + activityName + "?",
                "Confirm Registration",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.registerStudentForActivity(authResult.getUserId(), activityId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                                "Successfully registered for " + activityName,
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadAllActivities();
                        loadMyRegistrations();
                    }
                } catch (Exception ex) {
                    if (ex.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                                "Already registered for this activity.",
                                "Duplicate Registration",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                                "Error registering: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    /**
     * Handles Edit button click.
     * Opens dialog to change registration to another activity in the same event.
     */
    private void onEditClicked() {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a registration to edit.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participantId = (int) registrationsTableModel.getValueAt(selectedRow, 0);
        int activityId = (int) registrationsTableModel.getValueAt(selectedRow, 1);
        String activityName = (String) registrationsTableModel.getValueAt(selectedRow, 2);
        String eventName = (String) registrationsTableModel.getValueAt(selectedRow, 3);

        // Get event_id from activity
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                List<ActivityDTO> activities = dao.findAllActivities();
                for (ActivityDTO act : activities) {
                    if (act.getActivityId() == activityId) {
                        return act.getEventId();
                    }
                }
                return -1;
            }

            @Override
            protected void done() {
                try {
                    int eventId = get();
                    if (eventId == -1) {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                                "Could not find event for this activity.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ActivityRegistrationDialog dialog = new ActivityRegistrationDialog(
                        (Frame) SwingUtilities.getWindowAncestor(StudentDashboardPanel.this),
                        eventId,
                        activityId,
                        -1
                    );
                    dialog.setVisible(true);

                    ActivityDTO selectedActivity = dialog.getSelectedActivity();
                    if (selectedActivity != null && selectedActivity.getActivityId() != 0) {
                        int confirm = JOptionPane.showConfirmDialog(StudentDashboardPanel.this,
                                "Change registration from " + activityName + " to " + selectedActivity.getActivityName() + "?",
                                "Confirm Change",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            changeRegistration(participantId, selectedActivity.getActivityId(), selectedActivity.getActivityName());
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Changes a registration from one activity to another.
     */
    private void changeRegistration(int participantId, int newActivityId, String newActivityName) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.changeRegistration(participantId, newActivityId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                                "Successfully changed registration to " + newActivityName,
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadMyRegistrations();
                        loadAllActivities();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error changing registration: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handles Cancel Registration button click.
     */
    private void onCancelClicked() {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a registration to cancel.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int participantId = (int) registrationsTableModel.getValueAt(selectedRow, 0);
        String activityName = (String) registrationsTableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel registration for " + activityName + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.cancelRegistration(participantId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                                "Registration cancelled successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadMyRegistrations();
                        loadAllActivities();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Error cancelling registration: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Handles Register New button click.
     * Opens ActivitySelectionDialog to select and register for new activities.
     */
    private void onRegisterNewClicked() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ActivitySelectionDialog dialog = new ActivitySelectionDialog(parentFrame, authResult.getUserId());
        dialog.showDialog();
        
        // After dialog closes, refresh registrations
        loadMyRegistrations();
        loadAllActivities();
    }

    /**
     * Call this method when the panel becomes visible to load initial data.
     */
    public void onPanelShown() {
        loadEvents();
        loadAllActivities();
        loadMyRegistrations();
    }
}
