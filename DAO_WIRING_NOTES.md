# Swing UI Project - DAO Integration Notes

## Quick Reference: How Panels Wire to DAOs

Each panel follows a simple pattern:

### 1. Import DAO and Model
```java
import com.deptassoc.dao.AssociationDao;
import com.deptassoc.model.Association;
```

### 2. Create Table Model
```java
tableModel = new DefaultTableModel(
    new String[]{"ID", "Name", "Description"}, 0);
```

### 3. Load Data with SwingWorker (No Freezing!)
```java
SwingWorker<List<Association>, Void> worker = 
    new SwingWorker<List<Association>, Void>() {
    @Override
    protected List<Association> doInBackground() throws Exception {
        return new AssociationDao().findAll();  // Background thread
    }
    
    @Override
    protected void done() {
        List<Association> data = get();  // Back on EDT
        // Populate table here
    }
};
worker.execute();
```

### 4. Populate JTable
```java
for (Association assoc : associations) {
    tableModel.addRow(new Object[]{
        assoc.getAssocId(),
        assoc.getAssocName(),
        assoc.getDescription()
    });
}
```

---

## DAO Methods Available

All existing DAOs have these standard methods:

```java
// Universal methods (all DAOs have these)
List<Entity> findAll() throws SQLException
Entity findById(int id) throws SQLException
void insert(Entity entity) throws SQLException
void update(Entity entity) throws SQLException
void delete(int id) throws SQLException
```

### Special Methods by DAO

| DAO | Special Method | Returns |
|-----|---|---|
| EventDao | `findByAssociation(int assocId)` | `List<Event>` |
| ActivityDao | `findByEvent(int eventId)` | `List<Activity>` |
| ActivityParticipantDao | `findByActivity(int activityId)` | `List<ActivityParticipant>` |
| ActivityWinnerDao | `findByActivity(int activityId)` | `List<ActivityWinner>` |

---

## Model Getters (Correct Names)

Use these exact getter names when populating tables:

### Association
- `getAssocId()` - int
- `getAssocName()` - String
- `getDescription()` - String
- `getEstablishmentYear()` - int (NOT getFoundedYear)
- `getDepartmentId()` - int

### Event
- `getEventId()` - int (NOT getEId)
- `getEventName()` - String (NOT getEName)
- `getDescription()` - String
- `getEventDate()` - Date (NOT getEDate)
- `getVenue()` - String (NOT getLocation)
- `getParticipantCount()` - int
- `getAssocId()` - int

### Student
- `getStudentId()` - int (NOT getSId)
- `getSName()` - String
- `getSEmail()` - String
- `getPhone()` - String

### Activity
- `getActivityId()` - int (NOT getActId)
- `getActivityName()` - String (NOT getActName)
- `getDescription()` - String
- `getEventId()` - int
- `getStartTime()` - Time
- `getEndTime()` - Time
- `getParticipantCount()` - int

### ActivityParticipant
- `getParticipantId()` - int (NOT getApId)
- `getActivityId()` - int (NOT getActId)
- `getStudentId()` - int (NOT getSId)
- `getRegisteredOn()` - Timestamp (NOT getRegistrationDate)

### Faculty
- `getFId()` - int
- `getFName()` - String
- `getDeptName()` - String

### AssociationMember
- `getMemberId()` - int
- `getStudentId()` - int
- `getAssocId()` - int

### AssociationFacultyAdviser
- `getAdviserAssignmentId()` - int
- `getFacultyId()` - int
- `getAssocId()` - int

### ActivityWinner
- `getWinnerId()` - int
- `getActivityId()` - int
- `getStudentId()` - int
- `getWinningPosition()` - int

---

## Panel Templates

### Simple Read-Only Panel (e.g., StudentsPanel)
```java
public class YourPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public YourPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        tableModel = new DefaultTableModel(
            new String[]{"Column1", "Column2", "Column3"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Status bar with refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Loading...");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadData());
        bottomPanel.add(statusLabel);
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadData();
    }
    
    private void loadData() {
        SwingWorker<List<?>, Void> worker = new SwingWorker<...>() {
            protected List<?> doInBackground() throws Exception {
                statusLabel.setText("Loading...");
                return new YourDao().findAll();
            }
            
            protected void done() {
                try {
                    List<?> data = get();
                    tableModel.setRowCount(0);
                    for (var item : data) {
                        tableModel.addRow(new Object[]{...});
                    }
                    statusLabel.setText("Loaded " + data.size() + " items");
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
```

### Filtered Panel (e.g., ActivitiesPanel by Event)
```java
public class FilteredPanel extends JPanel {
    private JTextField filterField;
    
    public FilteredPanel() {
        // ... setup ...
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter ID:"));
        filterField = new JTextField(10);
        filterPanel.add(filterField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> loadFiltered());
        filterPanel.add(searchButton);
        add(filterPanel, BorderLayout.NORTH);
        
        // ... table setup ...
    }
    
    private void loadFiltered() {
        String idStr = filterField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a value", 
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(idStr);
        SwingWorker<List<?>, Void> worker = new SwingWorker<...>() {
            protected List<?> doInBackground() throws Exception {
                return new YourDao().findByParent(id);  // Filtered query
            }
            
            protected void done() {
                // Populate table
            }
        };
        worker.execute();
    }
}
```

---

## Role-Based Panel Assignment

Edit `MainFrame.addRoleBasedPanels()` to show different panels per role:

```java
private void addRoleBasedPanels() {
    String role = authResult.getRole();
    
    switch (role) {
        case "STUDENT":
            tabbedPane.addTab("Events", new EventsPanel());
            tabbedPane.addTab("Activities", new ActivitiesPanel());
            // Only visible to students
            break;
            
        case "FACULTY":
            tabbedPane.addTab("Associations", new AssociationsPanel());
            tabbedPane.addTab("Registrations", new RegistrationApprovalPanel());
            // Faculty-specific tabs
            break;
            
        case "ASSOCIATION_MEMBER":
            tabbedPane.addTab("Participants", new ParticipantsPanel());
            // Member management tabs
            break;
            
        case "ADMIN":
        default:
            tabbedPane.addTab("Associations", new AssociationsPanel());
            tabbedPane.addTab("Students", new StudentsPanel());
            tabbedPane.addTab("Events", new EventsPanel());
            tabbedPane.addTab("Activities", new ActivitiesPanel());
            tabbedPane.addTab("Participants", new ParticipantsPanel());
            // ADMIN sees everything
    }
}
```

---

## Common Patterns

### Error Handling Pattern
```java
try {
    List<?> data = get();
    // Process data
} catch (Exception e) {
    JOptionPane.showMessageDialog(this,
        "Error: " + e.getMessage(),
        "Database Error",
        JOptionPane.ERROR_MESSAGE);
}
```

### Table Column Sizing
```java
table.getColumnModel().getColumn(0).setPreferredWidth(40);
table.getColumnModel().getColumn(1).setPreferredWidth(150);
table.getColumnModel().getColumn(2).setPreferredWidth(300);
```

### Read-Only Table
```java
tableModel = new DefaultTableModel(...) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;  // Prevent editing
    }
};
```

### Single Row Selection
```java
table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
```

### Get Selected Row
```java
int selectedRow = table.getSelectedRow();
if (selectedRow >= 0) {
    Object value = tableModel.getValueAt(selectedRow, columnIndex);
}
```

---

## Database Connection

All DAOs automatically use `DBConnectionManager.getConnection()` which:
1. Reads from `resources/config.properties`
2. Loads MySQL JDBC driver
3. Returns a fresh connection for each operation

**No changes needed** - just call DAO methods and they handle connection pooling.

---

## No Schema Changes

✓ All panels use **read-only** operations (`findAll()`, `findById()`)
✓ No `insert()`, `update()`, `delete()` calls in UI yet (skeleton only)
✓ Database schema completely untouched
✓ All data flows through existing DAOs

---

## Troubleshooting DAO Wiring

| Problem | Solution |
|---------|----------|
| Compilation error: "cannot find symbol" | Check getter method names in model class |
| "SQLException: Connection refused" | Verify `config.properties` DB credentials |
| UI freezes when loading | Ensure SwingWorker is used (not direct DAO call on EDT) |
| No data displayed | Check `statusLabel.setText()` for error messages |
| "Cannot instantiate DAO" | Ensure DAO has default constructor (no args) |

---

## Example: Adding Faculty Panel

1. **Create `FacultyPanel.java`**:
```java
package com.deptassoc.swingui;

import com.deptassoc.dao.FacultyDao;
import com.deptassoc.model.Faculty;
import javax.swing.*;
import java.util.List;

public class FacultyPanel extends JPanel {
    // Follow StudentsPanel template
    private void loadFaculties() {
        SwingWorker<List<Faculty>, Void> worker = new SwingWorker<...>() {
            protected List<Faculty> doInBackground() throws Exception {
                return new FacultyDao().findAll();
            }
            
            protected void done() {
                for (Faculty f : get()) {
                    tableModel.addRow(new Object[]{
                        f.getFId(), 
                        f.getFName(), 
                        f.getDeptName()
                    });
                }
            }
        };
        worker.execute();
    }
}
```

2. **Add to `MainFrame.addRoleBasedPanels()`**:
```java
case "FACULTY":
    tabbedPane.addTab("Faculties", new FacultyPanel());  // NEW
    // ... other tabs
```

3. **Build & Run**:
```bash
build-swing-windows.bat
run-swing-windows.bat
```

---

## File Checklist

✓ `MainSwing.java` - Entry point
✓ `LoginDialog.java` - Login UI
✓ `MainFrame.java` - Main window with role-based tabs
✓ `AssociationsPanel.java` - Associations table
✓ `StudentsPanel.java` - Students table
✓ `EventsPanel.java` - Events table
✓ `ActivitiesPanel.java` - Activities by event
✓ `ParticipantsPanel.java` - Participants by activity
✓ `MyRegistrationsPanel.java` - Student registrations (skeleton)
✓ `RegistrationApprovalPanel.java` - Faculty approval (skeleton)
✓ `build-swing-windows.bat` - Build script
✓ `run-swing-windows.bat` - Run script

---

**Ready to extend!** Follow these patterns to add more panels and DAOs.
