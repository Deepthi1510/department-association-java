# Swing GUI Frontend - Integration Guide

## Overview

This document explains how the Swing GUI frontend integrates with your existing JDBC backend (package `com.deptassoc`). The frontend reuses all existing DAOs and models without any schema changes.

---

## Architecture

```
User
  ↓
LoginDialog (users.json authentication)
  ↓
MainFrame (role-based UI)
  ↓
Panels (EventsPanel, StudentsPanel, etc.)
  ↓
DAOs (existing: EventDao, StudentDao, etc.)
  ↓
DBConnectionManager (existing, uses config.properties)
  ↓
MySQL Database
```

---

## How to Wire to Existing DAOs

### 1. **Database Connection**

All panels use the **existing `DBConnectionManager`**:

```java
try (Connection conn = DBConnectionManager.getConnection()) {
    // Use connection
}
```

This reads from `resources/config.properties` automatically. No new connection code.

### 2. **DAO Usage Pattern**

Each panel creates a DAO instance and calls methods:

```java
// In AssociationsPanel.java
List<Association> associations = new AssociationDao().findAll();

// In EventsPanel.java
List<Event> events = new EventDao().findAll();
```

**No DAO modifications needed.** Existing DAOs work as-is:
- `findAll()` - returns List<Entity>
- `findById(id)` - returns single Entity
- `findByAssociation(id)` - returns filtered List<Entity>
- All CRUD methods intact

### 3. **SwingWorker for Async Operations**

To prevent UI freeze, all DB calls happen in `SwingWorker.doInBackground()`:

```java
SwingWorker<List<Association>, Void> worker = new SwingWorker<List<Association>, Void>() {
    @Override
    protected List<Association> doInBackground() throws Exception {
        return new AssociationDao().findAll();  // Runs on separate thread
    }
    
    @Override
    protected void done() {
        List<Association> result = get();      // Back on EDT
        populateTable(result);
    }
};
worker.execute();
```

### 4. **Error Handling**

Database errors are caught and displayed in dialog:

```java
try {
    List<Association> associations = get();
    populateTable(associations);
} catch (Exception e) {
    statusLabel.setText("Error: " + e.getMessage());
    JOptionPane.showMessageDialog(this, 
        "Error loading data: " + e.getMessage(), 
        "Database Error", 
        JOptionPane.ERROR_MESSAGE);
}
```

---

## Wiring a New DAO to the UI

### Step 1: Create a New Panel

```java
// Example: FacultyPanel.java
package com.deptassoc.swingui;

import com.deptassoc.dao.FacultyDao;
import com.deptassoc.model.Faculty;

public class FacultyPanel extends JPanel {
    private DefaultTableModel tableModel;
    
    public FacultyPanel() {
        // Create table with columns matching Faculty model
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Department"}, 0);
        table = new JTable(tableModel);
        // ... add to panel
        loadFaculties();
    }
    
    private void loadFaculties() {
        SwingWorker<List<Faculty>, Void> worker = 
            new SwingWorker<List<Faculty>, Void>() {
            @Override
            protected List<Faculty> doInBackground() throws Exception {
                return new FacultyDao().findAll();
            }
            
            @Override
            protected void done() {
                List<Faculty> faculties = get();
                for (Faculty f : faculties) {
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

### Step 2: Add Panel to MainFrame

```java
// In MainFrame.java, addRoleBasedPanels() method:
case "ADMIN":
    tabbedPane.addTab("Faculties", new FacultyPanel());
    // ...
```

---

## Existing DAOs Available

All these DAOs can be used directly in panels:

| DAO | Key Methods | Model |
|-----|-------------|-------|
| `AssociationDao` | `findAll()`, `findById()` | `Association` |
| `StudentDao` | `findAll()`, `findById()` | `Student` |
| `EventDao` | `findAll()`, `findById()`, `findByAssociation()` | `Event` |
| `ActivityDao` | `findAll()`, `findById()`, `findByEvent()` | `Activity` |
| `FacultyDao` | `findAll()`, `findById()` | `Faculty` |
| `ActivityParticipantDao` | `findAll()`, `findByActivity()` | `ActivityParticipant` |
| `ActivityWinnerDao` | `findAll()` | `ActivityWinner` |
| `AssociationMemberDao` | `findAll()`, `findById()` | `AssociationMember` |
| `AssociationFacultyAdviserDao` | `findAll()` | `AssociationFacultyAdviser` |

---

## Authentication Integration

### AuthManager

Uses existing `com.deptassoc.auth.AuthManager`:
- Reads `users.json`
- Authenticates credentials
- Returns `AuthResult` with role and user ID

### LoginDialog

```java
LoginDialog loginDialog = new LoginDialog();
AuthResult result = loginDialog.showDialog();  // Blocks until login

if (result.isSuccess()) {
    MainFrame mainFrame = new MainFrame(result);
    mainFrame.setVisible(true);
}
```

### AuthResult

Passed to `MainFrame` to determine role-specific UI:

```java
String role = authResult.getRole();  // "STUDENT", "FACULTY", etc.
int userId = authResult.getUserId();
String username = authResult.getUsername();
String displayName = authResult.getDisplayName();
```

---

## File Structure

```
src/com/deptassoc/swingui/
├── MainSwing.java              # Entry point
├── MainFrame.java              # Main window, role-based tabs
├── LoginDialog.java            # Login dialog modal
├── AssociationsPanel.java      # Associations table
├── StudentsPanel.java          # Students table
├── EventsPanel.java            # Events table
├── ActivitiesPanel.java        # Activities (filtered by event)
├── ParticipantsPanel.java      # Participants (filtered by activity)
├── MyRegistrationsPanel.java   # Student registrations (skeleton)
└── RegistrationApprovalPanel.java  # Faculty approval (skeleton)

build-swing-windows.bat         # Build script
run-swing-windows.bat           # Run script
```

---

## Build & Run

### Build

```bash
build-swing-windows.bat
```

Creates:
- `out/` directory with compiled classes
- `out/app-swing.jar` executable JAR
- Copies `lib/*.jar` to `out/lib/`
- Copies `resources/` to `out/`

### Run

```bash
run-swing-windows.bat
```

Sets classpath and launches:
```
java -cp "out;out/lib/*;out/config.properties" com.deptassoc.swingui.MainSwing
```

---

## Key Design Patterns

### 1. **No Database Schema Changes**

All existing tables, columns, and relationships are **untouched**. The Swing UI only reads/queries.

### 2. **Reuses Existing Code**

- Uses existing `DBConnectionManager`
- Uses existing DAOs (no reimplementation)
- Uses existing Models
- Uses existing `config.properties`
- Uses existing `AuthManager` & authentication

### 3. **SwingWorker Pattern**

All DB operations run on background thread to prevent UI freeze:
```
EDT (UI thread) → SwingWorker.doInBackground() (background thread)
                → SwingWorker.done() (back to EDT)
```

### 4. **Role-Based Access Control**

`MainFrame` accepts `AuthResult` and shows role-specific menus:
- **STUDENT**: Events, Activities, My Registrations
- **FACULTY**: Associations, Events, Registrations Approval
- **ASSOCIATION_MEMBER**: Associations, Events, Activities, Participants
- **ADMIN**: All panels

---

## Extending the UI

### Add a New Panel

1. Create `YourPanel.java` extending `JPanel`
2. In constructor, create `JTable` and `tableModel`
3. Add SwingWorker to load data from DAO
4. Add to `MainFrame.addRoleBasedPanels()` based on role

### Modify Main Frame Layout

Edit `MainFrame.java`:
```java
private void addRoleBasedPanels() {
    String role = authResult.getRole();
    // Add or remove tabs here
    tabbedPane.addTab("Name", new YourPanel());
}
```

### Add More DAOs

All DAOs follow same pattern:
```java
List<T> findAll() throws SQLException
T findById(int id) throws SQLException
void insert(T entity) throws SQLException
void update(T entity) throws SQLException
void delete(int id) throws SQLException
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "config.properties not found" | Ensure `resources/config.properties` exists in classpath (copied by build script) |
| "users.json not found" | Run: `java -cp out com.deptassoc.util.SetupUtil init-users` |
| "Connection refused" | Check `config.properties` DB credentials; ensure MySQL is running |
| UI freezes during DB load | Verify SwingWorker is used in panels (not direct DAO calls on EDT) |
| "Cannot find symbol: class MyDao" | Ensure DAO is in `src/com/deptassoc/dao/` and imports are correct |

---

## No Breaking Changes

✓ Existing console UI (`ConsoleUI.java`) still works  
✓ All DAOs unchanged  
✓ All models unchanged  
✓ Database schema untouched  
✓ Config files compatible  
✓ Authentication reused  

Both console and Swing UIs can coexist.

---

## Next Steps

1. Build: `build-swing-windows.bat`
2. Run: `run-swing-windows.bat`
3. Login with credentials from `users.json`
4. Explore role-based menus
5. Extend panels and add more DAOs as needed
