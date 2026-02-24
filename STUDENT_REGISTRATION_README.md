# Student Activity Registration Feature

## Overview
This document describes the complete student-facing activity registration feature added to the Java Swing + JDBC project.

## Files Added/Modified

### New Files Created:
1. `src/com/deptassoc/dao/ParticipantDao.java` - DAO for activity registrations
2. `src/com/deptassoc/dto/RegistrationDTO.java` - DTO for registration data
3. `src/com/deptassoc/dto/ActivityDTO.java` - DTO for activity data
4. `src/com/deptassoc/swingui/ActivityRegistrationDialog.java` - Dialog for editing registrations
5. `src/com/deptassoc/swingui/StudentDashboardPanel.java` - Main student UI with 3 tabs

### Integration Points:
- `StudentDashboardPanel` expects `AuthResult` object with `getId()` returning student_id
- Uses existing `EventDao` and `DBConnectionManager`
- All database operations use `PreparedStatement` with parameters

## Build & Run Instructions

### Step 1: Compile
```bash
cd c:\Users\deept\department-association-java
.\build-swing-windows.bat
```

Expected output:
```
[*] Compiling Java sources...
[OK] Compilation successful
[*] Copying resources...
[*] Copying libraries...
[*] Creating app-swing.jar...
[OK] Build successful
```

### Step 2: Run
```bash
.\run-swing-windows.bat
```

## Database Schema (No Changes Required)

All SQL queries use existing tables and columns:

**Tables Used:**
- `event(event_id, assoc_id, event_name, event_date, venue, description, participant_count)`
- `activity(activity_id, event_id, activity_name, description, start_time, end_time, participant_count)`
- `activity_participants(participant_id, activity_id, student_id, registered_on)`
- `student(student_id, s_name, s_email, phone)` [for reference]

**SQL Queries Used:**

1. **Get Events:**
```sql
SELECT event_id, event_name, description, event_date, venue FROM event ORDER BY event_date;
```

2. **Get Activities by Event:**
```sql
SELECT activity_id, activity_name, description, start_time, end_time, participant_count 
FROM activity WHERE event_id = ? ORDER BY start_time;
```

3. **Get All Activities:**
```sql
SELECT a.activity_id, a.activity_name, a.description, a.start_time, a.end_time, a.participant_count, e.event_name, e.event_id 
FROM activity a 
JOIN event e ON a.event_id = e.event_id 
ORDER BY e.event_date, a.start_time;
```

4. **Register Student:**
```sql
INSERT INTO activity_participants (activity_id, student_id, registered_on) VALUES (?, ?, CURRENT_TIMESTAMP);
```

5. **Find Student's Registrations:**
```sql
SELECT ap.participant_id, ap.activity_id, a.activity_name, e.event_name, ap.registered_on 
FROM activity_participants ap 
JOIN activity a ON ap.activity_id = a.activity_id 
JOIN event e ON a.event_id = e.event_id 
WHERE ap.student_id = ? 
ORDER BY ap.registered_on DESC;
```

6. **Find Other Activities in Same Event:**
```sql
SELECT activity_id, activity_name FROM activity WHERE event_id = ? AND activity_id <> ?;
```

7. **Cancel Registration:**
```sql
DELETE FROM activity_participants WHERE participant_id = ?;
```

8. **Update Activity Participant Count:**
```sql
SELECT COUNT(*) FROM activity_participants WHERE activity_id = ?;
UPDATE activity SET participant_count = ? WHERE activity_id = ?;
```

## Feature Details

### 1. Events Tab
- Displays all events in a JTable with columns: event_id, event_name, description, event_date, venue
- "View Activities" button filters activities for the selected event
- "Refresh" button reloads the events list
- Single row selection mode

### 2. Activities Tab
- Shows activities (filtered by event or all)
- Columns: activity_id, activity_name, description, start_time, end_time, participant_count
- "Register" button creates a new registration
- "Refresh" button reloads the activities list
- Handles duplicate registrations with friendly message: "Already registered"

### 3. My Registrations Tab
- Shows all activities the logged-in student is registered for
- Columns: participant_id, activity_id, activity_name, event_name, registered_on
- "Edit" button opens a dialog to change to another activity in the same event
- "Cancel Registration" button removes the registration with confirmation
- "Refresh" button reloads the registrations list

## Transaction Handling

All write operations use explicit transaction control:
- `setAutoCommit(false)` before DML
- Multiple related updates are wrapped in a single transaction
- Automatic rollback on exception
- `setAutoCommit(true)` restored after operation

Example: Changing a registration atomically deletes the old registration, inserts the new one, and updates participant counts for both activities.

## Error Handling

- `SQLIntegrityConstraintViolationException` caught for duplicate registrations
- All exceptions show user-friendly `JOptionPane` messages
- Database connection failures are logged and reported
- Validation: Prevents operations if no row is selected

## Thread Safety

All long-running database operations use `SwingWorker`:
- Database queries run on background thread
- UI updates occur on Event Dispatch Thread (EDT)
- UI remains responsive during database operations

## Testing Scenario

Assuming sample data exists in the database:

### Test Sequence:

**Prerequisites:**
- Student exists in `student` table (e.g., student_id=1, s_name="Alice", s_email="alice@example.com")
- Event exists in `event` table (e.g., event_id=1, event_name="Tech Summit 2025")
- Activities exist in `activity` table for that event (e.g., activity_id=1 and activity_id=2)

**Steps:**

1. **Log in as Student:**
   - Run the application
   - Login dialog appears (if using LoginDialog from earlier 3-type system)
   - Select "Student" from dropdown
   - Enter username: "Alice" (matches s_name)
   - Enter password: "alice@example.com" (matches s_email)
   - Click "Login"

2. **Browse Events:**
   - StudentDashboardPanel opens with 3 tabs
   - Switch to "Events" tab
   - Table shows all events
   - Select "Tech Summit 2025"
   - Click "View Activities"

3. **Register for Activity:**
   - Automatically switches to "Activities" tab
   - Table shows activities from "Tech Summit 2025"
   - Select first activity (e.g., "Workshop on AI")
   - Click "Register"
   - Confirmation dialog appears
   - Click "Yes"
   - Success message: "Successfully registered for Workshop on AI"
   - Tables refresh automatically

4. **View Registration:**
   - Switch to "My Registrations" tab
   - Table shows the new registration with participant_id, activity details, and registration timestamp

5. **Change Registration:**
   - In "My Registrations" tab, select the registration
   - Click "Edit"
   - ActivityRegistrationDialog opens showing other activities in the same event
   - Select another activity (e.g., "Networking Session")
   - Click "Change"
   - Confirmation dialog appears
   - Click "Yes"
   - Success message: "Successfully changed registration to Networking Session"
   - My Registrations table updates

6. **Cancel Registration:**
   - In "My Registrations" tab, select a registration
   - Click "Cancel Registration"
   - Confirmation dialog: "Cancel registration for Networking Session?"
   - Click "Yes"
   - Success message: "Registration cancelled successfully"
   - My Registrations table updates

7. **Try Duplicate Registration:**
   - Switch to "Activities" tab
   - Select the activity already registered for
   - Click "Register"
   - Confirmation dialog appears
   - Click "Yes"
   - Error message: "Already registered for this activity."
   - No change made to database

## Code Organization

```
src/com/deptassoc/
├── dao/
│   ├── ParticipantDao.java (NEW)
│   ├── EventDao.java (existing)
│   └── [other DAOs]
├── dto/
│   ├── RegistrationDTO.java (NEW)
│   ├── ActivityDTO.java (NEW)
│   └── [other DTOs]
├── swingui/
│   ├── StudentDashboardPanel.java (NEW)
│   ├── ActivityRegistrationDialog.java (NEW)
│   └── [other UI components]
├── db/
│   └── DBConnectionManager.java (existing)
└── model/
    ├── Event.java (existing)
    ├── Activity.java (existing)
    └── [other models]
```

## Integration with MainFrame / MainSwing

To integrate StudentDashboardPanel into your main application:

1. In `MainFrame.java` constructor, add the StudentDashboardPanel to the tabbed pane:
```java
if ("STUDENT".equals(authResult.getRole())) {
    StudentDashboardPanel studentPanel = new StudentDashboardPanel(authResult);
    tabbedPane.addTab("Dashboard", studentPanel);
    // Call onPanelShown after panel is visible
    tabbedPane.addChangeListener(e -> {
        if (tabbedPane.getSelectedComponent() == studentPanel) {
            studentPanel.onPanelShown();
        }
    });
}
```

2. Ensure `AuthResult` has `getId()` method returning student_id.

## Dependencies

- Java 17+
- MySQL Connector/J 8.x (mysql-connector-java-8.x.jar)
- Existing DBConnectionManager with connection pooling
- Existing EventDao for event queries

## Performance Notes

- Participant count updates are done with simple COUNT queries (not stored procedures)
- If you have the stored procedure `update_event_unique_participant_count(event_id)`, uncomment the relevant code in ParticipantDao.updateActivityParticipantCount()
- All queries use PreparedStatement for security and performance

## Known Limitations

- ActivityRegistrationDialog only supports changing to activities in the same event
- No batch registration (only one activity at a time)
- Participant counts are updated per activity (not aggregated at event level unless procedure is called)

## Troubleshooting

**Issue: "Already registered" message when first registering**
- Check if a row already exists in activity_participants table
- Clear test data if needed

**Issue: Activities not showing in Activities tab after clicking View Activities**
- Ensure the selected event has activities in the activity table
- Check foreign key relationship: activity.event_id → event.event_id

**Issue: Database connection null errors**
- Verify config.properties has correct database connection details
- Check DBConnectionManager initialization
- Ensure MySQL server is running

**Issue: Compilation errors**
- Verify EventDao exists with findAll() method
- Verify AuthResult class has getId() method
- Check that all DTOs and DAOs are in correct packages
