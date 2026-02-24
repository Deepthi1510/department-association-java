package com.deptassoc.ui.faculty;

import com.deptassoc.dao.FacultyDao;
import com.deptassoc.dto.ActivityDTO;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;

/**
 * Dialog for adding a new activity to an event.
 */
public class AddActivityDialog extends JDialog {
    private JTextField activityNameField;
    private JTextArea descriptionArea;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JButton addButton;
    private JButton cancelButton;
    private ActivityDTO createdActivity;
    private int eventId;

    public AddActivityDialog(Frame owner, int eventId) {
        super(owner, "Add Activity", true);
        this.eventId = eventId;
        this.createdActivity = null;

        initUI();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Activity Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Activity Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        activityNameField = new JTextField(20);
        mainPanel.add(activityNameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(descScrollPane, gbc);

        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Start Time (HH:MM:SS):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        startTimeField = new JTextField(20);
        mainPanel.add(startTimeField, gbc);

        // End Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("End Time (HH:MM:SS):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        endTimeField = new JTextField(20);
        mainPanel.add(endTimeField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> onAddClicked());
        cancelButton.addActionListener(e -> onCancelClicked());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void onAddClicked() {
        String activityName = activityNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String startTimeStr = startTimeField.getText().trim();
        String endTimeStr = endTimeField.getText().trim();

        if (activityName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Activity name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Time startTime;
        Time endTime;
        try {
            startTime = Time.valueOf(startTimeStr);
            endTime = Time.valueOf(endTimeStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Time format must be HH:MM:SS", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create DTO and insert
        ActivityDTO dto = new ActivityDTO(0, activityName, description, startTime, endTime, 0);
        dto.setEventId(eventId);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                FacultyDao dao = new FacultyDao();
                return dao.addActivity(dto);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        createdActivity = dto;
                        JOptionPane.showMessageDialog(AddActivityDialog.this,
                                "Activity added successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(AddActivityDialog.this,
                                "Failed to add activity.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AddActivityDialog.this,
                            "Error adding activity: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void onCancelClicked() {
        createdActivity = null;
        dispose();
    }

    public ActivityDTO getCreatedActivity() {
        return createdActivity;
    }
}
