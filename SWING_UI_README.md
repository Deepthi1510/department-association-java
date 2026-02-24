# Swing GUI Frontend - README

## Overview

This directory now includes a complete **Java Swing GUI frontend** that connects to your existing JDBC backend. The Swing UI complements the console UI (`ConsoleUI.java`) and provides a modern graphical interface.

- **Package**: `com.deptassoc.swingui`
- **Entry Point**: `MainSwing.java`
- **Database**: Reuses existing `DBConnectionManager` (reads from `config.properties`)
- **DAOs**: Reuses all existing DAOs (no modifications needed)
- **Authentication**: Uses existing `AuthManager` and `users.json`
- **Database Schema**: No changes - read-only operations

---

## Quick Start

### 1. Build
```bash
build-swing-windows.bat
```

### 2. Initialize Users
```bash
java -cp out com.deptassoc.util.SetupUtil init-users
```

### 3. Run
```bash
run-swing-windows.bat
```

### 4. Login
Use credentials from `users.json` (default: alice/password123)

---

## Project Structure

### Swing UI Components

```
src/com/deptassoc/swingui/
├── MainSwing.java              Entry point, initializes auth & launches MainFrame
├── LoginDialog.java            Modal login dialog (username/password)
├── MainFrame.java              Main application window with role-based tabs
├── AssociationsPanel.java      JTable displaying associations (AssociationDao)
├── StudentsPanel.java          JTable displaying students (StudentDao)
├── EventsPanel.java            JTable displaying events (EventDao)
├── ActivitiesPanel.java        JTable displaying activities by event (ActivityDao)
├── ParticipantsPanel.java      JTable displaying participants by activity (ActivityParticipantDao)
├── MyRegistrationsPanel.java   Student's registered activities (skeleton)
└── RegistrationApprovalPanel.java Faculty registration approval (skeleton)
```

### Build & Run Scripts

```
build-swing-windows.bat        Compiles all sources, creates app-swing.jar
run-swing-windows.bat          Runs the Swing application
```

### Documentation

```
SWING_UI_INTEGRATION.md        How to wire DAOs to UI panels
SWING_UI_QUICKSTART.md         60-second quick start guide
```

---

## How It Works

### 1. Authentication Flow

```
User starts app
  ↓
LoginDialog prompts username/password
  ↓
AuthManager validates against users.json
  ↓
On success → AuthResult with role (STUDENT/FACULTY/ASSOCIATION_MEMBER/ADMIN)
  ↓
MainFrame displays role-specific UI
```

### 2. Role-Based UI

| Role | Visible Panels | Permissions |
|------|---|---|
| STUDENT | Events, Activities, My Registrations | View events & activities, register |
| FACULTY | Associations, Events, Registrations Approval | View associations, approve registrations |
| ASSOCIATION_MEMBER | Associations, Events, Activities, Participants | Create/manage events & activities |
| ADMIN | All panels | Full access to all data |

### 3. Database Operations (No UI Freeze)

All data loading happens on a background thread using `SwingWorker`:

```java
SwingWorker<List<Association>, Void> worker = new SwingWorker<...>() {
    @Override
    protected List<Association> doInBackground() throws Exception {
        return new AssociationDao().findAll();  // Background thread
    }
    
    @Override
    protected void done() {
        List<Association> result = get();       // EDT (UI thread)
        populateTable(result);
    }
};
worker.execute();
```

This ensures the UI stays responsive while loading data.

### 4. Error Handling

Database errors are caught and shown in user-friendly dialogs:

```java
catch (Exception e) {
    JOptionPane.showMessageDialog(this,
        "Error loading data: " + e.getMessage(),
        "Database Error",
        JOptionPane.ERROR_MESSAGE);
}
```

---

## Key Files Explained

### MainSwing.java
- **Purpose**: Entry point
- **What it does**:
  - Sets system look & feel
  - Initializes `AuthManager` (loads users.json)
  - Shows `LoginDialog` (modal)
  - On login success → creates `MainFrame` with `AuthResult`
  - On login failure → exits

### LoginDialog.java
- **Purpose**: Modal login prompt
- **Features**:
  - Username & password fields
  - "Login" button (Enter key also works)
  - 5-attempt limit
  - Shows remaining attempts
  - Returns `AuthResult` on success

### MainFrame.java
- **Purpose**: Main application window
- **Features**:
  - Accepts `AuthResult` in constructor
  - Creates role-specific tabbed pane
  - Menu bar (File → Exit, View → User Info, Help → About)
  - Calls `addRoleBasedPanels()` to show appropriate tabs

### Panel Classes (AssociationsPanel, StudentsPanel, etc.)
- **Pattern**: Each extends `JPanel`
- **Components**: 
  - `JTable` for displaying data
  - `DefaultTableModel` for table data
  - SwingWorker for loading
  - Refresh button to reload data
- **DAO Integration**: Each calls appropriate DAO (e.g., `new AssociationDao().findAll()`)

---

## Reusing Existing DAOs

All existing DAOs work without modification:

```java
// AssociationDao
List<Association> associations = new AssociationDao().findAll();
Association assoc = new AssociationDao().findById(1);

// EventDao
List<Event> events = new EventDao().findAll();
List<Event> byAssoc = new EventDao().findByAssociation(assocId);

// ActivityDao
List<Activity> activities = new ActivityDao().findAll();
List<Activity> byEvent = new ActivityDao().findByEvent(eventId);

// StudentDao
List<Student> students = new StudentDao().findAll();
Student student = new StudentDao().findById(1);

// ActivityParticipantDao
List<ActivityParticipant> participants = new ActivityParticipantDao().findByActivity(actId);
```

See `SWING_UI_INTEGRATION.md` for complete wiring examples.

---

## Building & Running

### Build

```bash
build-swing-windows.bat
```

**What it does**:
1. Creates `out/` directory
2. Compiles all Java sources (all packages including swingui)
3. Copies resources to `out/`
4. Copies libs to `out/lib/`
5. Creates `out/app-swing.jar` with Main-Class manifest

**Output**:
```
out/
├── app-swing.jar (executable JAR)
├── config.properties
└── lib/
    └── mysql-connector-j-*.jar
```

### Run

```bash
run-swing-windows.bat
```

**What it does**:
1. Verifies `app-swing.jar` exists
2. Sets classpath: `out;out/lib/*;out/config.properties`
3. Runs: `java -cp <classpath> com.deptassoc.swingui.MainSwing`

---

## Configuration

### Database Connection

Edit `resources/config.properties`:

```properties
db.host=localhost
db.port=3306
db.name=department_association_v2
db.user=root
db.password=YourPassword
```

The Swing UI automatically picks up these settings via `DBConnectionManager`.

### User Credentials

Users are stored in `users.json` (generated by `SetupUtil.init-users`):

```json
[
  {
    "type": "STUDENT",
    "id": 1,
    "username": "alice",
    "passwordHash": "base64salt:base64hash"
  },
  ...
]
```

To create users:
```bash
java -cp out com.deptassoc.util.SetupUtil init-users
```

---

## Extending the UI

### Add a New Panel

1. **Create** `YourPanel.java`:
```java
package com.deptassoc.swingui;

import javax.swing.*;
import java.util.List;

public class YourPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    public YourPanel() {
        setLayout(new BorderLayout(10, 10));
        
        // Create table
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", ...}, 0);
        table = new JTable(tableModel);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }
    
    private void loadData() {
        SwingWorker<List<?>, Void> worker = new SwingWorker<...>() {
            protected List<?> doInBackground() throws Exception {
                return new YourDao().findAll();
            }
            protected void done() {
                // Populate table
            }
        };
        worker.execute();
    }
}
```

2. **Add to MainFrame**:
```java
case "ADMIN":
    tabbedPane.addTab("Your Data", new YourPanel());
```

### Add a New DAO Panel

Follow the pattern in `AssociationsPanel.java`:
1. Create DAO instance
2. Call method inside SwingWorker
3. Populate JTable in `done()` method
4. Add refresh button

---

## Features

✓ **Modal Login Dialog** - Prompts for credentials, enforces 5-attempt limit  
✓ **Role-Based Menus** - Different tabs per role (STUDENT/FACULTY/MEMBER/ADMIN)  
✓ **JTable Display** - Read-only tables with sorting/filtering  
✓ **SwingWorker Pattern** - Background DB operations, no UI freeze  
✓ **Error Dialogs** - User-friendly database error messages  
✓ **Existing DAO Reuse** - No DAO modifications, drop-in integration  
✓ **Authentication** - Uses existing AuthManager & users.json  
✓ **No Schema Changes** - Database completely untouched  
✓ **Config Management** - Respects config.properties  

---

## No Breaking Changes

✓ Console UI (`ConsoleUI.java`) still works  
✓ All DAOs unchanged  
✓ All models unchanged  
✓ Database schema untouched  
✓ Existing authentication reused  
✓ Both UIs can coexist  

---

## File Tree

```
.
├── src/com/deptassoc/
│   ├── auth/                 (existing: AuthManager, AuthResult)
│   ├── dao/                  (existing: 9 DAOs)
│   ├── db/                   (existing: DBConnectionManager)
│   ├── model/                (existing: 9 models)
│   ├── ui/                   (existing: ConsoleUI, LoginUI)
│   ├── util/                 (existing: PasswordUtil, SetupUtil)
│   └── swingui/              (NEW: Swing UI components)
│       ├── MainSwing.java
│       ├── MainFrame.java
│       ├── LoginDialog.java
│       ├── AssociationsPanel.java
│       ├── StudentsPanel.java
│       ├── EventsPanel.java
│       ├── ActivitiesPanel.java
│       ├── ParticipantsPanel.java
│       ├── MyRegistrationsPanel.java
│       └── RegistrationApprovalPanel.java
├── build-swing-windows.bat   (NEW)
├── run-swing-windows.bat     (NEW)
├── SWING_UI_INTEGRATION.md   (NEW)
└── SWING_UI_QUICKSTART.md    (NEW)
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Cannot find config.properties" | Ensure build script copies `resources/` to `out/` |
| "users.json not found" | Run: `java -cp out com.deptassoc.util.SetupUtil init-users` |
| "Connection refused to MySQL" | Check `config.properties` credentials; ensure MySQL service is running |
| "UI freezes when loading data" | Verify SwingWorker is used (not direct DAO calls on EDT) |
| "Cannot find DAO class" | Ensure DAO is in `src/com/deptassoc/dao/` and build included it |
| "Compilation error in swingui" | Check JDK version (Java 17+ recommended); verify all imports |

---

## Next Steps

1. **Build**: `build-swing-windows.bat`
2. **Setup**: `java -cp out com.deptassoc.util.SetupUtil init-users`
3. **Run**: `run-swing-windows.bat`
4. **Explore**: Login and test role-based panels
5. **Extend**: Add more panels/DAOs using existing patterns
6. **Deploy**: Copy `out/` directory to production

---

## Reference Documentation

- **Integration Guide**: See `SWING_UI_INTEGRATION.md` for DAO wiring patterns
- **Quick Start**: See `SWING_UI_QUICKSTART.md` for 60-second setup
- **Console UI**: Original console UI still available in `ConsoleUI.java`
- **Build**: See `build-swing-windows.bat` for compilation details

---

**Ready to go!** 🚀

For questions on wiring DAOs, see `SWING_UI_INTEGRATION.md`.
