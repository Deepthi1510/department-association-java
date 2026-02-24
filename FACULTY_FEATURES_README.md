# Faculty Features - Build & Test Instructions

## Files Created

```
src/com/deptassoc/
├── dao/
│   └── FacultyDao.java (UPDATED - added 5 new methods)
├── dto/
│   ├── EventDTO.java (NEW)
│   ├── StudentDTO.java (NEW)
│   └── ActivityDTO.java (existing)
└── ui/faculty/
    ├── FacultyDashboardPanel.java (NEW)
    └── AddActivityDialog.java (NEW)
```

## Build

```bash
cd c:\Users\deept\department-association-java
.\build-swing-windows.bat
```

**Expected Output:**
```
[*] Compiling Java sources...
[OK] Compilation successful
[OK] Build successful
[INFO] JAR created: out\app-swing.jar
```

## Run

```bash
.\run-swing-windows.bat
```

## Faculty Testing Workflow

### 1. Login as Faculty
- Select "Faculty" from login type dropdown
- Enter faculty username and password
- Application opens MainFrame

### 2. My Events Tab
- Faculty sees all events assigned to them
- Table columns: Event ID, Event Name, Date, Venue, Description
- Query uses association_faculty_advisers table to find assigned events
- Refresh button reloads the list

### 3. Participants Tab
- Select an event from "Event" dropdown
- Activities under that event load in "Activity" dropdown
- Select an activity
- All students registered for that activity appear in the table
- Table columns: Student ID, Name, Email, Phone, Registered On
- Refresh button reloads event list

### 4. Manage Activities Tab
- Select an event from event dropdown
- All activities for that event load in table
- Table columns: Activity ID, Activity Name, Description, Start Time, End Time, Participants

**Add Activity:**
- Click "Add Activity" button
- Dialog opens with fields:
  - Activity Name (text field)
  - Description (multi-line text area)
  - Start Time (format: HH:MM:SS, e.g., 09:00:00)
  - End Time (format: HH:MM:SS, e.g., 11:00:00)
- Click "Add" button
- New activity inserted into database
- Table refreshes automatically
- Success message shown

**Delete Activity:**
- Select activity from table
- Click "Delete Activity" button
- Confirmation dialog appears
- Click "Yes" to confirm
- Activity deleted from database (if no participants)
- If participants exist, error shown: "Cannot delete — participants already registered."
- Table refreshes automatically

## Database Tables (No Schema Changes)

```sql
-- Tables used (EXACT names, no modifications):
event(event_id, assoc_id, event_name, event_date, venue, description, participant_count)
activity(activity_id, event_id, activity_name, description, start_time, end_time, participant_count)
activity_participants(participant_id, activity_id, student_id, registered_on)
student(student_id, s_name, s_email, phone)
faculty(faculty_id, f_name, f_email, phone, designation)
association_faculty_advisers(assoc_id, faculty_id)  -- Used for faculty assignment
```

## SQL Queries Used

### 1. Get Events for Faculty
```sql
SELECT e.event_id, e.event_name, e.event_date, e.venue, e.description
FROM event e
JOIN association_faculty_advisers afa ON e.assoc_id = afa.assoc_id
WHERE afa.faculty_id = ?
ORDER BY e.event_date;
```

### 2. Get Activities for Event
```sql
SELECT activity_id, activity_name, description, start_time, end_time, participant_count
FROM activity
WHERE event_id = ?
ORDER BY start_time;
```

### 3. Get Participants for Activity
```sql
SELECT s.student_id, s.s_name, s.s_email, s.phone, ap.registered_on
FROM activity_participants ap
JOIN student s ON ap.student_id = s.student_id
WHERE ap.activity_id = ?
ORDER BY ap.registered_on DESC;
```

### 4. Add Activity
```sql
INSERT INTO activity (event_id, activity_name, description, start_time, end_time, participant_count)
VALUES (?, ?, ?, ?, ?, 0);
```

### 5. Delete Activity
```sql
DELETE FROM activity WHERE activity_id = ?;
```

## Integration with MainFrame

Update `src/com/deptassoc/swingui/MainFrame.java`:

In `addRoleBasedPanels()` method, add for FACULTY role:

```java
else if ("FACULTY".equals(role)) {
    FacultyDashboardPanel facultyPanel = new FacultyDashboardPanel(authResult);
    tabbedPane.addTab("Dashboard", facultyPanel);
    
    tabbedPane.addChangeListener(e -> {
        if (tabbedPane.getSelectedComponent() == facultyPanel) {
            facultyPanel.onPanelShown();
        }
    });
}
```

Import statement needed:
```java
import com.deptassoc.ui.faculty.FacultyDashboardPanel;
```

## Features Implemented

- ✓ My Events tab - Faculty sees all assigned events
- ✓ Participants tab - View participants for activities
- ✓ Manage Activities tab - Add/delete activities
- ✓ SwingWorker threading - No UI freeze during DB operations
- ✓ PreparedStatement - All SQL parameterized
- ✓ Error handling - Constraint violations, null checks
- ✓ Confirmation dialogs - Delete operations confirmed
- ✓ Refresh buttons - Manual reload of data

## Build Status

✓ Compilation: SUCCESSFUL (zero errors)
✓ JAR Size: 2,600,327 bytes (2.600 MB)
✓ Ready to run and test
