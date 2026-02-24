# Swing GUI - Quick Start

## 60-Second Launch

### 1. Build (20 seconds)
```bash
build-swing-windows.bat
```

### 2. Create Users (10 seconds)
```bash
java -cp out com.deptassoc.util.SetupUtil init-users
```

### 3. Run (5 seconds)
```bash
run-swing-windows.bat
```

---

## Test Credentials

| Username | Password | Role |
|----------|----------|------|
| alice | password123 | STUDENT |
| bob | password123 | STUDENT |
| drsmith | password123 | FACULTY |
| ashok | password123 | ASSOCIATION_MEMBER |

---

## What You'll See

### Login Screen
- Username/password fields
- 5 attempt limit
- Real-time feedback

### Main Window (Role-Specific)

**STUDENT**
- Events: View all events (JTable)
- Activities: Search and view activities by event
- My Registrations: Show enrolled activities

**FACULTY**
- Associations: View all associations
- Events: View all events
- Registrations: Approve pending registrations (skeleton)

**ASSOCIATION_MEMBER**
- Associations: Manage associations
- Events: Create/view events
- Activities: Create/view activities
- Participants: View activity participants

**ADMIN**
- Associations: Full access
- Students: View all students (JTable)
- Events: Full access
- Activities: Full access
- Participants: Full access

---

## Features

✓ **SwingWorker**: DB operations on background thread (no UI freeze)
✓ **JTable**: Read-only data display with sorting/filtering
✓ **Error Dialogs**: Database errors shown in popups
✓ **Role-Based Access**: Different menus per role
✓ **Existing DAOs**: Reuses all existing DAO classes
✓ **Authentication**: Uses same users.json + SHA-256 hashing

---

## File Structure

```
src/com/deptassoc/swingui/
├── MainSwing.java              ← Entry point
├── MainFrame.java              ← Main window
├── LoginDialog.java            ← Login modal
├── AssociationsPanel.java      ← Associations table
├── StudentsPanel.java          ← Students table
├── EventsPanel.java            ← Events table
├── ActivitiesPanel.java        ← Activities table
├── ParticipantsPanel.java      ← Participants table
├── MyRegistrationsPanel.java   ← Student registrations
└── RegistrationApprovalPanel.java ← Faculty approval

build-swing-windows.bat         ← Compile & create JAR
run-swing-windows.bat           ← Run the app
```

---

## Verify Installation

After `build-swing-windows.bat`:

```
out/
├── app-swing.jar ✓
├── config.properties ✓
├── lib/
│   └── mysql-connector-j-*.jar ✓
└── com/
    └── deptassoc/
        ├── auth/ ✓
        ├── dao/  ✓
        ├── db/   ✓
        ├── model/ ✓
        ├── ui/   ✓
        ├── util/ ✓
        └── swingui/ ✓ (NEW)
```

---

## Common Tasks

### Add a New Panel

1. Create `YourPanel.java` in `src/com/deptassoc/swingui/`
2. Extend `JPanel` and use `SwingWorker` for DB calls
3. Add to `MainFrame.java` in `addRoleBasedPanels()` method

### Change Database Connection

Edit `resources/config.properties`:
```properties
db.host=your-host
db.port=3306
db.name=your-db
db.user=root
db.password=your-password
```

### Modify User Roles

Edit `users.json`:
```json
{
  "type": "STUDENT",
  "id": 1,
  "username": "alice",
  "passwordHash": "..."
}
```

---

## Troubleshooting

**"App won't start"**
- Ensure MySQL is running
- Check `config.properties` credentials
- Verify `users.json` exists: `SetupUtil init-users`

**"UI freezes when loading"**
- Panels use SwingWorker, so shouldn't freeze
- If it does, check database connection isn't hanging

**"Can't find data"**
- Verify database connection works
- Run `config.properties` test:
  ```bash
  java -cp out com.deptassoc.db.DBConnectionManager
  ```

---

## Architecture Notes

- **No DB schema changes**: Only reads existing tables
- **Reuses existing DAOs**: No DAO reimplementation
- **Swing threading**: SwingWorker pattern prevents UI freeze
- **Role-based UI**: Same AuthManager as console app
- **Error handling**: Database errors shown in dialogs

---

## Next Steps

1. ✓ Build: `build-swing-windows.bat`
2. ✓ Setup: `java -cp out com.deptassoc.util.SetupUtil init-users`
3. ✓ Run: `run-swing-windows.bat`
4. → Explore role-based panels
5. → Extend with new panels/DAOs
6. → Deploy to production

---

**Ready to go!** 🚀
