# 3-Type Login System - Implementation Guide

## Overview

This update replaces the file-based `users.json` authentication with a **direct database-driven login system**. The login dialog now supports 3 user types based on your existing database tables.

**Key Changes:**
- No hashing - plain-text matching against database values
- 3-type login: Student, Faculty, Association Member
- Uses existing tables: `student`, `faculty`, `association_members`
- SwingWorker for async authentication (no UI freeze)
- No database schema changes

---

## Database Tables Used

### 1. Student Login
```sql
SELECT student_id, s_name, s_email FROM student 
WHERE s_name = ? AND s_email = ?
```

**Fields:**
- `s_name` → Username
- `s_email` → Password

**Returns:** student_id, s_name

### 2. Faculty Login
```sql
SELECT faculty_id, f_name, f_email FROM faculty 
WHERE f_name = ? AND f_email = ?
```

**Fields:**
- `f_name` → Username
- `f_email` → Password

**Returns:** faculty_id, f_name

### 3. Association Member Login
```sql
SELECT s.student_id, s.s_name, am.assoc_id FROM student s
INNER JOIN association_members am ON s.student_id = am.student_id
WHERE s.s_name = ? AND s.s_email = ?
```

**Fields:**
- `s_name` → Username (from student)
- `s_email` → Password (from student)
- **Must exist in `association_members` table**

**Returns:** student_id, s_name, assoc_id

---

## Files Created/Modified

### New Files (in `src/com/deptassoc/swingui/`)

1. **AuthResult.java** - Result object containing user info
2. **AuthService.java** - Authentication service with DB queries

### Modified Files (in `src/com/deptassoc/swingui/`)

1. **LoginDialog.java** - 3-type login UI with dropdown
2. **MainFrame.java** - Updated to use new AuthResult
3. **MainSwing.java** - Entry point (updated imports)

---

## File Locations

```
src/com/deptassoc/swingui/
├── AuthResult.java          ← NEW
├── AuthService.java         ← NEW
├── LoginDialog.java         ← MODIFIED
├── MainFrame.java           ← MODIFIED
├── MainSwing.java           ← MODIFIED
├── AssociationsPanel.java
├── EventsPanel.java
├── ActivitiesPanel.java
├── StudentsPanel.java
├── ParticipantsPanel.java
├── MyRegistrationsPanel.java
├── RegistrationApprovalPanel.java
└── EventsPanel.java
```

---

## Updated Code Files

### AuthResult.java (NEW)

```java
package com.deptassoc.swingui;

public class AuthResult {
    private boolean success;
    private String role;        // STUDENT, FACULTY, ASSOCIATION_MEMBER
    private int userId;
    private String username;
    private String displayName;
    private int assocId;        // For ASSOCIATION_MEMBER only

    public AuthResult(boolean success, String role, int userId, String username, String displayName) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.assocId = -1;
    }

    public AuthResult(boolean success, String role, int userId, String username, String displayName, int assocId) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.assocId = assocId;
    }

    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public int getAssocId() { return assocId; }

    @Override
    public String toString() {
        return String.format("AuthResult{role='%s', userId=%d, username='%s'}", role, userId, username);
    }
}
```

### AuthService.java (NEW)

```java
package com.deptassoc.swingui;

import com.deptassoc.db.DBConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Authentication service using direct database lookups.
 * Supports 3 login types: Student, Faculty, Association Member
 * Uses plain-text matching based on database values (no hashing).
 */
public class AuthService {

    public static AuthResult authenticate(String loginType, String username, String password) {
        try {
            switch (loginType) {
                case "STUDENT":
                    return authenticateStudent(username, password);
                case "FACULTY":
                    return authenticateFaculty(username, password);
                case "ASSOCIATION_MEMBER":
                    return authenticateAssociationMember(username, password);
                default:
                    return new AuthResult(false, null, 0, username, null);
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return new AuthResult(false, null, 0, username, null);
        }
    }

    private static AuthResult authenticateStudent(String username, String password) throws Exception {
        String sql = "SELECT student_id, s_name, s_email FROM student WHERE s_name = ? AND s_email = ?";
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String sName = rs.getString("s_name");
                    return new AuthResult(true, "STUDENT", studentId, sName, sName);
                }
            }
        }
        
        return new AuthResult(false, null, 0, username, null);
    }

    private static AuthResult authenticateFaculty(String username, String password) throws Exception {
        String sql = "SELECT faculty_id, f_name, f_email FROM faculty WHERE f_name = ? AND f_email = ?";
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int facultyId = rs.getInt("faculty_id");
                    String fName = rs.getString("f_name");
                    return new AuthResult(true, "FACULTY", facultyId, fName, fName);
                }
            }
        }
        
        return new AuthResult(false, null, 0, username, null);
    }

    private static AuthResult authenticateAssociationMember(String username, String password) throws Exception {
        String sql = "SELECT s.student_id, s.s_name, am.member_id, am.assoc_id " +
                     "FROM student s " +
                     "INNER JOIN association_members am ON s.student_id = am.student_id " +
                     "WHERE s.s_name = ? AND s.s_email = ?";
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String sName = rs.getString("s_name");
                    int assocId = rs.getInt("assoc_id");
                    return new AuthResult(true, "ASSOCIATION_MEMBER", studentId, sName, sName, assocId);
                }
            }
        }
        
        return new AuthResult(false, null, 0, username, null);
    }
}
```

### LoginDialog.java (MODIFIED)

Key changes:
- Added JComboBox for login type selection
- 3 options: "Student", "Faculty", "Association Member"
- Calls AuthService instead of AuthManager
- Username field now matches name columns (s_name, f_name)
- Password field now matches email columns (s_email, f_email)
- SwingWorker for async authentication

### MainFrame.java (MODIFIED)

Key changes:
- Simplified role-based panel selection (3 roles only, no ADMIN)
- Updated title to show display name
- Role-specific menus:
  - **STUDENT**: Events, Activities, My Registrations
  - **FACULTY**: Associations, Events, Registrations
  - **ASSOCIATION_MEMBER**: Associations, Events, Activities, Participants

### MainSwing.java (MODIFIED)

Key change:
- Removed `AuthManager.init()` call
- Now directly uses new LoginDialog with AuthService

---

## Login Flow

```
User starts app
    ↓
LoginDialog shows 3-type selector
    ↓
User selects: Student / Faculty / Association Member
    ↓
User enters Username (name) & Password (email)
    ↓
Click Login button
    ↓
SwingWorker calls AuthService.authenticate()
    ↓
Database lookup on background thread
    ↓
SUCCESS → AuthResult returned → MainFrame opens with role-specific menu
FAILURE → "Invalid credentials" message → User can retry
```

---

## Authentication Details

### Student Login
- Query: `SELECT * FROM student WHERE s_name = ? AND s_email = ?`
- Username = `s_name`
- Password = `s_email`

### Faculty Login
- Query: `SELECT * FROM faculty WHERE f_name = ? AND f_email = ?`
- Username = `f_name`
- Password = `f_email`

### Association Member Login
- Query: `SELECT * FROM student s INNER JOIN association_members am ON s.student_id = am.student_id WHERE s.s_name = ? AND s.s_email = ?`
- Username = `s_name` (must be in both student AND association_members)
- Password = `s_email`
- **Requirement:** Student must have a record in `association_members` table

---

## Build & Run

### Build
```bash
build-swing-windows.bat
```

### Run
```bash
run-swing-windows.bat
```

### Or Run Directly
```bash
java -cp "out;out/lib/*;out/config.properties" com.deptassoc.swingui.MainSwing
```

---

## Test Data Examples

### Student Login
- **Username**: Alice (s_name)
- **Password**: alice@university.edu (s_email)

### Faculty Login
- **Username**: Dr. Smith (f_name)
- **Password**: smith@university.edu (f_email)

### Association Member Login
- **Username**: Bob (s_name)
- **Password**: bob@university.edu (s_email)
- **Requirement**: Bob must exist in both `student` and `association_members` tables

---

## Error Handling

**Invalid Credentials**
- Database lookup returns no results
- Shows: "Invalid credentials. Please try again."
- User can retry

**Database Connection Error**
- SQLException during authentication
- Shows: "Error: [error message]"
- Check `config.properties` DB credentials

**Association Member Not Found**
- Student credentials match but not in association_members
- Shows: "Invalid credentials. Please try again."
- Must add student to association_members table first

---

## No Schema Changes

✓ Uses existing `student` table (no columns added)
✓ Uses existing `faculty` table (no columns added)
✓ Uses existing `association_members` table (no columns added)
✓ No new tables created
✓ PreparedStatements prevent SQL injection

---

## Role-Specific Menus

### STUDENT Menu
| Tab | Description |
|-----|---|
| Events | View all events |
| Activities | View activities for selected event |
| My Registrations | View student's registered activities |

### FACULTY Menu
| Tab | Description |
|-----|---|
| Associations | View all associations |
| Events | View all events |
| Registrations | Approve pending registrations (skeleton) |

### ASSOCIATION_MEMBER Menu
| Tab | Description |
|-----|---|
| Associations | Manage associations |
| Events | View/manage events |
| Activities | View/manage activities |
| Participants | View activity participants |

---

## Key Files Summary

| File | Purpose | Status |
|------|---------|--------|
| AuthResult.java | Result object with user info | NEW |
| AuthService.java | DB-driven authentication | NEW |
| LoginDialog.java | 3-type login UI | MODIFIED |
| MainFrame.java | Role-specific UI | MODIFIED |
| MainSwing.java | Entry point | MODIFIED |
| DBConnectionManager.java | DB connection (unchanged) | EXISTING |

---

## Next Steps

1. **Verify your database tables have required data:**
   - At least 1 record in `student` table (with s_name, s_email)
   - At least 1 record in `faculty` table (with f_name, f_email)
   - At least 1 record linking student to association_members

2. **Build and test:**
   ```bash
   build-swing-windows.bat
   run-swing-windows.bat
   ```

3. **Try each login type:**
   - Select "Student", enter student name & email
   - Select "Faculty", enter faculty name & email
   - Select "Association Member", enter student name & email (must be in association_members)

4. **Add more users** by inserting records directly into database tables

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Invalid credentials" always shows | Verify database values match exactly (case-sensitive) |
| Cannot connect to database | Check `config.properties` host, port, credentials |
| Application hangs during login | May be slow DB connection; check network |
| "user_id not found" error | Ensure `student_id` or `faculty_id` columns exist |

---

**Complete! All files ready to use.** 🚀
