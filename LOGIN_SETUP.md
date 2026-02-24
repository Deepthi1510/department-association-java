# Role-Based Login System - Setup Guide

## Overview

The application now includes a **role-based login system** with the following roles:
- **STUDENT**: Can register for activities, view results
- **FACULTY**: Can advise associations and events, view participants
- **ASSOCIATION_MEMBER**: Can create events/activities, manage participants
- **ADMIN** (no role specified): Full access to all features

## Initial Setup

### Step 1: Create Initial Users

Create a `users.json` file in the project root with sample users.

**Option A: Auto-generate sample users**

Add this to `Main.java` temporarily (before calling `AuthManager.init()`):

```java
// Uncomment ONCE to create initial users:
// AuthManager.createInitialUsers();
```

Then compile and run. After first run, comment it back out.

**Option B: Manual creation**

Create `users.json` in project root:

```json
[
  {
    "type": "STUDENT",
    "id": 1,
    "username": "alice",
    "passwordHash": "salt:hash"
  },
  {
    "type": "FACULTY",
    "id": 1,
    "username": "drsmith",
    "passwordHash": "salt:hash"
  },
  {
    "type": "ASSOCIATION_MEMBER",
    "id": 1,
    "username": "ashok",
    "passwordHash": "salt:hash"
  }
]
```

### Step 2: Generate Password Hashes

Use the `PasswordUtil` class to generate hashes:

```java
String passwordHash = PasswordUtil.hash("your_password");
System.out.println("Hash: " + passwordHash);
```

Then copy the hash into `users.json`.

### Step 3: Update Database IDs (Critical!)

The `id` field must match actual IDs in your database:
- For `STUDENT`: use actual `student.student_id`
- For `FACULTY`: use actual `faculty.faculty_id`
- For `ASSOCIATION_MEMBER`: use actual `association_members.member_id`

Example: If your DB has a student with `student_id = 5`, set `"id": 5` in `users.json`.

## Running the App

### Windows
```batch
build-windows.bat
run-windows.bat
```

### Unix/Linux/macOS
```bash
chmod +x build-unix.sh run-unix.sh
./build-unix.sh
./run-unix.sh
```

### Console Flow

```
════════════════════════════════════════
   Department Association System
════════════════════════════════════════

👤 Please Login

Username: alice
Password: ________

✓ Login successful!
Welcome, Alice Johnson (STUDENT)

========== MAIN MENU (STUDENT) ==========
1. View Events (Read-only)
2. View Activities (Read-only)
3. Register for Activity
4. View My Participation Results
5. View Winners
6. View Reports
7. Logout
```

## File Security

⚠️ **IMPORTANT**: Protect `users.json` file permissions:

### Windows
```powershell
icacls users.json /inheritance:r /grant:r "%USERNAME%:F"
```

### Unix/Linux/macOS
```bash
chmod 600 users.json
```

This ensures only the current user can read/write the file.

## User Management

### Adding Users at Runtime

1. Login as ADMIN (role-based system will allow if no role is set)
2. See admin menu option to "Create User"
3. Follow prompts to add username, select role, and set password

### Password Hashing Algorithm

- **Default**: SHA-256 with 16-byte random salt (base64 encoded)
- **Format**: `base64(salt):base64(hash)`
- **Optional**: Enable BCrypt by adding `org.mindrot:jbcrypt:0.4` to classpath

To enable BCrypt:
1. Add JAR to `lib/` directory
2. Uncomment BCrypt methods in `PasswordUtil.java`

### Password Policy

- Minimum 8 characters recommended
- Passwords are salted and hashed
- Never stored in plain text
- Max 5 failed login attempts per session

## Role-Based Access Control

| Feature | STUDENT | FACULTY | ASSOC_MEMBER | ADMIN |
|---------|---------|---------|--------------|-------|
| Manage Associations | ✓ (read) | ✓ (read) | ✓ (all) | ✓ |
| Manage Faculties | ✓ (read) | ✓ (read) | ✗ | ✓ |
| Manage Students | ✓ (read) | ✓ (read) | ✗ | ✓ |
| Manage Events | ✓ (read) | ✓ (advise) | ✓ (all) | ✓ |
| Manage Activities | ✓ (read) | ✓ (read) | ✓ (all) | ✓ |
| Register Participant | ✓ | ✗ | ✓ | ✓ |
| Add Winner | ✗ | ✗ | ✓ | ✓ |
| View Reports | ✓ | ✓ | ✓ | ✓ |

## Troubleshooting

### "No users configured"
- Create `users.json` in project root
- Run `AuthManager.createInitialUsers()` from code or manually populate

### "Invalid credentials"
- Check username/password in `users.json`
- Verify `id` matches database records
- Check password hash is correct format: `salt:hash`

### Maximum login attempts exceeded
- The app exits after 5 failed attempts for security
- Restart the application to try again

### "You don't have permission"
- Check your role in `users.json`
- Verify your role allows the requested operation
- See Role-Based Access Control table above

## Sample Users Configuration

Create `users.json` with these pre-configured users:

```json
[
  {
    "type": "STUDENT",
    "id": 1,
    "username": "student1",
    "passwordHash": "jB/K+nPVvLnpB9X3qL2mKg==:EJ8Eg4m8qP/ZZ5LjA7D7O9zH3kF8L2M5p9Q4w6X8YzN3/Z0=="
  },
  {
    "type": "FACULTY",
    "id": 1,
    "username": "faculty1",
    "passwordHash": "vW3nK+zPqL8mN5O2R9S6Tv==:X1y2Z3a4B5c6D7e8F9g0H1i2J3k4L5m6N7o8P9q0R1s2T3u4V5w6"
  },
  {
    "type": "ASSOCIATION_MEMBER",
    "id": 1,
    "username": "member1",
    "passwordHash": "aB1cD2eF3gH4iJ5kL6mN7oP==:Q8r9S0tUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGh"
  }
]
```

**⚠️ WARNING**: These are dummy hashes for sample purposes only!
Replace with real hashes generated by `PasswordUtil.hash(password)`.

## Code Changes Summary

### New Files
- `src/com/deptassoc/auth/AuthResult.java` - Auth result POJO
- `src/com/deptassoc/auth/AuthManager.java` - Authentication manager
- `src/com/deptassoc/ui/LoginUI.java` - Login console interface
- `src/com/deptassoc/util/PasswordUtil.java` - Password hashing utility

### Modified Files
- `src/com/deptassoc/Main.java` - Added login flow
- `src/com/deptassoc/ui/ConsoleUI.java` - Added role-based menus and access control

### New Config Files
- `users.json` - User credentials (generated/manual)
- `users-sample.json` - Sample user template

## No Breaking Changes

✓ All existing DAOs, models, and database operations remain unchanged
✓ No SQL schema modifications
✓ Backward compatible if ConsoleUI used without AuthResult
✓ All existing features still work, now with role-based access

## Next Steps

1. Create `users.json` with your users
2. Build: `build-windows.bat` or `./build-unix.sh`
3. Run: `run-windows.bat` or `./run-unix.sh`
4. Login with your credentials
5. Use role-specific menus
