# Student Registration Feature - Code Reference

## Directory Structure
```
src/com/deptassoc/
├── dao/
│   ├── ParticipantDao.java (NEW) ......................... 417 lines
│   ├── EventDao.java (existing)
│   └── [other DAOs]
├── dto/
│   ├── ActivityDTO.java (NEW) ............................ 82 lines
│   ├── RegistrationDTO.java (NEW) ........................ 54 lines
│   └── [other DTOs]
├── swingui/
│   ├── StudentDashboardPanel.java (NEW) .................. 568 lines
│   ├── ActivityRegistrationDialog.java (NEW) ............. 132 lines
│   ├── MainFrame.java (existing)
│   ├── MainSwing.java (existing)
│   └── [other UI components]
├── auth/
├── db/
├── model/
├── ui/
└── util/
```

## ParticipantDao.java - Method Signatures

```java
public class ParticipantDao {
    // Query: GET registrations by student
    public List<RegistrationDTO> findRegistrationsByStudent(int studentId) throws SQLException
    
    // DML: REGISTER student (transactional)
    public boolean registerStudentForActivity(int studentId, int activityId) throws SQLException
    
    // DML: CANCEL registration (transactional)
    public boolean cancelRegistration(int participantId) throws SQLException
    
    // Query: GET activities by event
    public List<ActivityDTO> findActivitiesByEvent(int eventId) throws SQLException
    
    // Query: GET all activities
    public List<ActivityDTO> findAllActivities() throws SQLException
    
    // Query: GET other activities in event (for edit dialog)
    public List<ActivityDTO> findOtherActivitiesInEvent(int eventId, int excludeActivityId) throws SQLException
    
    // DML: CHANGE registration (transactional)
    public boolean changeRegistration(int participantId, int newActivityId) throws SQLException
    
    // Helper: UPDATE participant count
    private void updateActivityParticipantCount(Connection conn, int activityId) throws SQLException
}
```

## RegistrationDTO.java - Fields

```java
public class RegistrationDTO {
    private int participantId;      // PK from activity_participants
    private int activityId;         // FK to activity
    private String activityName;    // from activity.activity_name
    private String eventName;       // from event.event_name
    private Timestamp registeredOn; // from activity_participants.registered_on
    
    // Full constructor + getters/setters
}
```

## ActivityDTO.java - Fields

```java
public class ActivityDTO {
    private int activityId;         // PK from activity
    private String activityName;    // from activity.activity_name
    private String description;     // from activity.description
    private Time startTime;         // from activity.start_time
    private Time endTime;           // from activity.end_time
    private int participantCount;   // from activity.participant_count
    private int eventId;            // FK to event (optional)
    private String eventName;       // from event.event_name (optional)
    
    // Full constructor + getters/setters
}
```

## ActivityRegistrationDialog.java - Key Methods

```java
public class ActivityRegistrationDialog extends JDialog {
    // Constructor takes: Frame owner, eventId, currentActivityId, currentActivityName
    public ActivityRegistrationDialog(Frame owner, int eventId, int currentActivityId, int currentActivityName)
    
    // Call this to get selected activity after dialog closes
    public ActivityDTO getSelectedActivity()
    
    // Private methods:
    private void initUI()                              // Build dialog UI
    private void loadOtherActivities()                 // SwingWorker query
    private void onChangeButtonClicked()               // Handle change button
    private void onCancelButtonClicked()               // Handle cancel button
}
```

## StudentDashboardPanel.java - Key Methods

```java
public class StudentDashboardPanel extends JPanel {
    // Constructor: takes AuthResult with getUserId() method
    public StudentDashboardPanel(AuthResult authResult)
    
    // Call this after panel becomes visible to load initial data
    public void onPanelShown()
    
    // Tab creation:
    private JPanel createEventsPanel()                 // Events tab
    private JPanel createActivitiesPanel()             // Activities tab
    private JPanel createMyRegistrationsPanel()        // My Registrations tab
    
    // Data loading (all use SwingWorker):
    private void loadEvents()                          // Load all events
    private void loadActivitiesByEvent(int eventId)    // Load activities for event
    private void loadAllActivities()                   // Load all activities
    private void loadMyRegistrations()                 // Load student's registrations
    
    // Button handlers:
    private void onViewActivitiesClicked()             // Events tab: View Activities
    private void onRegisterClicked()                   // Activities tab: Register
    private void onEditClicked()                       // My Registrations tab: Edit
    private void onCancelClicked()                     // My Registrations tab: Cancel
    
    // Change operation:
    private void changeRegistration(int participantId, int newActivityId, String newActivityName)
}
```

## Database Tables Used (No Changes)

```sql
-- TABLE: event
-- PK: event_id
-- Used columns: event_id, event_name, description, event_date, venue
CREATE TABLE event (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    assoc_id INT,
    event_name VARCHAR(255),
    event_date DATE,
    venue VARCHAR(255),
    description TEXT,
    participant_count INT DEFAULT 0,
    FOREIGN KEY (assoc_id) REFERENCES association(assoc_id)
);

-- TABLE: activity
-- PK: activity_id
-- FK: event_id → event.event_id
-- Used columns: activity_id, event_id, activity_name, description, start_time, end_time, participant_count
CREATE TABLE activity (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    activity_name VARCHAR(255),
    description TEXT,
    start_time TIME,
    end_time TIME,
    participant_count INT DEFAULT 0,
    FOREIGN KEY (event_id) REFERENCES event(event_id)
);

-- TABLE: activity_participants
-- PK: participant_id
-- FKs: activity_id → activity.activity_id, student_id → student.student_id
-- Used columns: participant_id, activity_id, student_id, registered_on
-- UNIQUE: (activity_id, student_id)
CREATE TABLE activity_participants (
    participant_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT NOT NULL,
    student_id INT NOT NULL,
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    UNIQUE KEY unique_registration (activity_id, student_id)
);

-- TABLE: student (existing)
-- Used columns: student_id (for FK only in this feature)
CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    s_name VARCHAR(255),
    s_email VARCHAR(255),
    phone VARCHAR(20),
    ...
);
```

## Transaction Flow Diagrams

### Registration Flow (registerStudentForActivity)
```
1. Get Connection, setAutoCommit(false)
2. INSERT INTO activity_participants (activity_id, student_id, registered_on)
3. SELECT COUNT(*) FROM activity_participants WHERE activity_id = ?
4. UPDATE activity SET participant_count = ? WHERE activity_id = ?
5. conn.commit()
6. setAutoCommit(originalAutoCommit), close Connection
```

### Change Registration Flow (changeRegistration)
```
1. Get Connection, setAutoCommit(false)
2. SELECT student_id, activity_id FROM activity_participants WHERE participant_id = ?
3. DELETE FROM activity_participants WHERE participant_id = ?
4. INSERT INTO activity_participants (activity_id, student_id, registered_on) [new activity]
5. UPDATE activity SET participant_count WHERE activity_id = ? [old activity]
6. UPDATE activity SET participant_count WHERE activity_id = ? [new activity]
7. conn.commit()
8. setAutoCommit(originalAutoCommit), close Connection
```

### Cancel Registration Flow (cancelRegistration)
```
1. Get Connection, setAutoCommit(false)
2. SELECT activity_id FROM activity_participants WHERE participant_id = ?
3. DELETE FROM activity_participants WHERE participant_id = ?
4. UPDATE activity SET participant_count WHERE activity_id = ?
5. conn.commit()
6. setAutoCommit(originalAutoCommit), close Connection
```

## SQL Statements (Exact)

### 1. Find Registrations by Student
```sql
SELECT ap.participant_id, ap.activity_id, a.activity_name, e.event_name, ap.registered_on 
FROM activity_participants ap 
JOIN activity a ON ap.activity_id = a.activity_id 
JOIN event e ON a.event_id = e.event_id 
WHERE ap.student_id = ? 
ORDER BY ap.registered_on DESC
```

### 2. Register Student
```sql
INSERT INTO activity_participants (activity_id, student_id, registered_on) VALUES (?, ?, CURRENT_TIMESTAMP)
```

### 3. Find Activities by Event
```sql
SELECT activity_id, activity_name, description, start_time, end_time, participant_count FROM activity WHERE event_id = ? ORDER BY start_time
```

### 4. Find All Activities
```sql
SELECT a.activity_id, a.activity_name, a.description, a.start_time, a.end_time, a.participant_count, e.event_name, e.event_id 
FROM activity a 
JOIN event e ON a.event_id = e.event_id 
ORDER BY e.event_date, a.start_time
```

### 5. Find Other Activities in Event
```sql
SELECT activity_id, activity_name FROM activity WHERE event_id = ? AND activity_id <> ?
```

### 6. Delete Registration
```sql
DELETE FROM activity_participants WHERE participant_id = ?
```

### 7. Get Activity Count
```sql
SELECT COUNT(*) FROM activity_participants WHERE activity_id = ?
```

### 8. Update Activity Count
```sql
UPDATE activity SET participant_count = ? WHERE activity_id = ?
```

## Exception Handling Strategy

```java
// In SwingWorker.done():
try {
    T result = get();  // May throw ExecutionException (wraps SQLException)
    // Process result
} catch (Exception ex) {
    if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
        // Duplicate registration (unique constraint violation)
        JOptionPane.showMessageDialog(..., "Already registered", ...);
    } else if (ex instanceof SQLException) {
        // Database error
        JOptionPane.showMessageDialog(..., "Error: " + ex.getMessage(), ...);
    } else {
        // Other errors
        JOptionPane.showMessageDialog(..., "Unexpected error", ...);
    }
}
```

## Connection Management Pattern

```java
// In ParticipantDao methods:
Connection conn = DBConnectionManager.getConnection();
if (conn == null) {
    throw new SQLException("Database connection is null");
}

boolean originalAutoCommit = true;
try {
    originalAutoCommit = conn.getAutoCommit();
    conn.setAutoCommit(false);
    
    // ... perform DML operations ...
    
    conn.commit();
    return true;
} catch (SQLException e) {
    try {
        conn.rollback();
    } catch (SQLException rollbackEx) {
        rollbackEx.printStackTrace();
    }
    throw e;
} finally {
    try {
        conn.setAutoCommit(originalAutoCommit);
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

## UI Component Hierarchy

```
StudentDashboardPanel (main panel with JTabbedPane)
├── Tab 1: Events Panel
│   ├── JScrollPane
│   │   └── JTable (eventsTable)
│   └── JPanel (buttonPanel)
│       ├── JButton "View Activities"
│       └── JButton "Refresh"
├── Tab 2: Activities Panel
│   ├── JScrollPane
│   │   └── JTable (activitiesTable)
│   └── JPanel (buttonPanel)
│       ├── JButton "Register"
│       └── JButton "Refresh"
└── Tab 3: My Registrations Panel
    ├── JScrollPane
    │   └── JTable (registrationsTable)
    └── JPanel (buttonPanel)
        ├── JButton "Edit"
        ├── JButton "Cancel Registration"
        └── JButton "Refresh"

ActivityRegistrationDialog (modal dialog)
├── JPanel (mainPanel)
│   ├── JLabel "Select another activity:"
│   ├── JComboBox<ActivityDTO> (activityComboBox)
│   └── JPanel (buttonPanel)
│       ├── JButton "Change"
│       └── JButton "Cancel"
```

## Build Command

```bash
# Change to project directory
cd c:\Users\deept\department-association-java

# Run build script (compiles all Java files including new DTO and DAO)
.\build-swing-windows.bat

# Expected output:
# [*] Compiling Java sources...
# [OK] Compilation successful
# [OK] Build successful
# [INFO] JAR created: out\app-swing.jar
```

## Total Lines of Code

```
ParticipantDao.java ........................ 417 lines (DAO with 8 methods)
RegistrationDTO.java ....................... 54 lines (POJO)
ActivityDTO.java ........................... 82 lines (POJO)
ActivityRegistrationDialog.java ........... 132 lines (Modal dialog)
StudentDashboardPanel.java ................ 568 lines (Main UI with 3 tabs)
─────────────────────────────────────────────────────
TOTAL NEW CODE ............................. 1,253 lines

Plus modified:
build-swing-windows.bat (added dto directory to javac compile line)
```

## Testing Checklist

- [ ] Build succeeds with zero compilation errors
- [ ] JAR created successfully
- [ ] Login as student
- [ ] Events tab loads all events
- [ ] View Activities button filters correctly
- [ ] Activities tab shows all/filtered activities
- [ ] Register button creates new registration
- [ ] Duplicate registration shows friendly error
- [ ] My Registrations tab shows student's registrations
- [ ] Edit button opens dialog with available activities
- [ ] Change button updates registration to new activity
- [ ] Cancel button deletes registration
- [ ] Participant counts update correctly
- [ ] Refresh buttons reload tables
- [ ] Confirmation dialogs appear for destructive operations
- [ ] Error messages appear for invalid selections
- [ ] No SQL errors in database logs
- [ ] Transactions are working (rollback on error)
