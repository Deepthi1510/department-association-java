package com.deptassoc.swingui;

import com.deptassoc.dao.ParticipantDao;
import com.deptassoc.dto.ActivityDTO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dialog for editing/changing a student's activity registration.
 * Shows other activities in the same event and allows selection for transfer.
 */
public class ActivityRegistrationDialog extends JDialog {
    private JComboBox<ActivityDTO> activityComboBox;
    private JButton changeButton;
    private JButton cancelButton;
    private ActivityDTO selectedActivity;
    private int eventId;
    private int currentActivityId;

    public ActivityRegistrationDialog(Frame owner, int eventId, int currentActivityId, int currentActivityName) {
        super(owner, "Change Registration", true);
        this.eventId = eventId;
        this.currentActivityId = currentActivityId;
        this.selectedActivity = null;

        initUI();
        loadOtherActivities();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 200);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Select another activity:"), gbc);

        // ComboBox
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        activityComboBox = new JComboBox<>();
        mainPanel.add(activityComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changeButton = new JButton("Change");
        cancelButton = new JButton("Cancel");

        changeButton.addActionListener(e -> onChangeButtonClicked());
        cancelButton.addActionListener(e -> onCancelButtonClicked());

        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void loadOtherActivities() {
        new SwingWorker<List<ActivityDTO>, Void>() {
            @Override
            protected List<ActivityDTO> doInBackground() throws Exception {
                ParticipantDao dao = new ParticipantDao();
                return dao.findOtherActivitiesInEvent(eventId, currentActivityId);
            }

            @Override
            protected void done() {
                try {
                    List<ActivityDTO> activities = get();
                    for (ActivityDTO activity : activities) {
                        activityComboBox.addItem(activity);
                    }

                    if (activities.isEmpty()) {
                        changeButton.setEnabled(false);
                        activityComboBox.addItem(new ActivityDTO(0, "No other activities available", "", null, null, 0));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ActivityRegistrationDialog.this,
                            "Error loading activities: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void onChangeButtonClicked() {
        selectedActivity = (ActivityDTO) activityComboBox.getSelectedItem();
        if (selectedActivity != null && selectedActivity.getActivityId() != 0) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid activity.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCancelButtonClicked() {
        selectedActivity = null;
        dispose();
    }

    public ActivityDTO getSelectedActivity() {
        return selectedActivity;
    }
}
