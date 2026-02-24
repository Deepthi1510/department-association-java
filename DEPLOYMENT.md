DEPLOYMENT CHECKLIST & QUICK REFERENCE
=======================================

## Files to Deploy

### NEW PACKAGES & CLASSES (7 files total)

✓ NEW AUTH PACKAGE (2 files):
  - src/com/deptassoc/auth/AuthResult.java
  - src/com/deptassoc/auth/AuthManager.java

✓ NEW UTIL PACKAGE (2 files):
  - src/com/deptassoc/util/PasswordUtil.java
  - src/com/deptassoc/util/SetupUtil.java

✓ NEW UI FILE (1 file):
  - src/com/deptassoc/ui/LoginUI.java

✓ NEW CONFIG FILES (3 files):
  - users-sample.json (template)
  - users.json (generated at runtime)
  - .gitignore (updated)

### MODIFIED EXISTING FILES (5 files)

✓ CORE:
  - src/com/deptassoc/Main.java (CHANGED)
  - src/com/deptassoc/ui/ConsoleUI.java (CHANGED)

✓ BUILD:
  - build-windows.bat (CHANGED)
  - build-unix.sh (CHANGED)

✓ DOCS:
  - README.md (CHANGED)

### DOCUMENTATION (3 new files)

✓ NEW DOCS:
  - LOGIN_SETUP.md (setup guide)
  - IMPLEMENTATION_SUMMARY.md (technical summary)
  - CHANGES.md (detailed changelog)

---

## DEPLOYMENT STEPS

### Step 1: Backup (Optional but Recommended)
```bash
# Backup your current project
cp -r department-association-java department-association-java.backup
```

### Step 2: Copy New Files
Copy all NEW files listed above to their exact locations.

### Step 3: Update Existing Files
Replace the 5 MODIFIED files with the new versions provided.

### Step 4: Verify Structure
```
src/com/deptassoc/
├── Main.java (CHANGED)
├── auth/
│   ├── AuthResult.java (NEW)
│   └── AuthManager.java (NEW)
├── db/
│   ├── DBConnectionManager.java (unchanged)
│   └── SQLConstants.java (unchanged)
├── dao/ (9 files, all unchanged)
├── model/ (9 files, all unchanged)
├── ui/
│   ├── ConsoleUI.java (CHANGED)
│   └── LoginUI.java (NEW)
└── util/
    ├── PasswordUtil.java (NEW)
    └── SetupUtil.java (NEW)
```

### Step 5: Create Initial Users
```bash
# Option A: Auto-generate sample users
java -cp out com.deptassoc.util.SetupUtil init-users

# Option B: Manually create users.json from template
cp users-sample.json users.json
# Edit users.json: replace IDs and regenerate password hashes
```

### Step 6: Build Project
```bash
# Windows
build-windows.bat

# Unix/Linux/macOS
chmod +x build-unix.sh
./build-unix.sh
```

### Step 7: Run Application
```bash
# Windows
run-windows.bat

# Unix/Linux/macOS
chmod +x run-unix.sh
./run-unix.sh
```

### Step 8: Test Login
```
Username: alice (or your created username)
Password: (enter password)
Expected: Login successful, role-based menu appears
```

---

## QUICK REFERENCE

### Generate Password Hash
```bash
java -cp out com.deptassoc.util.SetupUtil hash "mypassword"
```
Output: `salt:hash` (copy into users.json)

### Create Initial Users
```bash
java -cp out com.deptassoc.util.SetupUtil init-users
```
Creates: users.json with sample users

### View Generated users.json Structure
```json
[
  {
    "type": "STUDENT",
    "id": 1,
    "username": "alice",
    "passwordHash": "salt:hash"
  }
]
```

### User Types Available
- STUDENT (restricted menu)
- FACULTY (advising features)
- ASSOCIATION_MEMBER (management features)
- (no type = ADMIN = full access)

### Default Sample Users Created
```
Username: alice     Type: STUDENT        ID: 1
Username: bob       Type: STUDENT        ID: 2
Username: drsmith   Type: FACULTY        ID: 1
Username: ashok     Type: ASSOCIATION_MEMBER ID: 1
All default password: password123 (CHANGE AFTER FIRST LOGIN)
```

---

## SECURITY SETUP (IMPORTANT!)

### Protect users.json File

**Unix/Linux/macOS**:
```bash
chmod 600 users.json
```

**Windows PowerShell** (as admin):
```powershell
icacls users.json /inheritance:r /grant:r "$($env:USERNAME):F"
```

**Never commit users.json to git** (already in .gitignore)

---

## TROUBLESHOOTING

### "No users configured" on startup
1. Create users.json (see Step 5 above)
2. Or run: `java -cp out com.deptassoc.util.SetupUtil init-users`

### "Invalid credentials"
1. Check username spelling in users.json
2. Verify password hash is correct format: `salt:hash`
3. Check ID matches database record

### "You don't have permission"
1. Check your role in users.json (STUDENT/FACULTY/MEMBER/ADMIN)
2. See permission table in LOGIN_SETUP.md

### Build fails
1. Ensure all NEW files are in correct directories
2. Ensure build scripts include auth and util packages (they should)
3. Check Java version: `java -version` (requires 17+)

### Login attempts exceeded
1. Restart the application
2. Check max attempts limit (5 per session)

---

## FILE LOCATIONS REFERENCE

### Authentication & Security
```
src/com/deptassoc/auth/
  ├── AuthResult.java
  └── AuthManager.java

src/com/deptassoc/util/
  ├── PasswordUtil.java
  └── SetupUtil.java
```

### User Interface
```
src/com/deptassoc/ui/
  ├── ConsoleUI.java (MODIFIED)
  └── LoginUI.java
```

### Configuration
```
root/
  ├── users.json (generated)
  ├── users-sample.json (template)
  ├── config.properties (unchanged)
  └── .gitignore (MODIFIED - protects users.json)
```

### Documentation
```
root/
  ├── LOGIN_SETUP.md (NEW - setup guide)
  ├── IMPLEMENTATION_SUMMARY.md (NEW - technical)
  ├── CHANGES.md (NEW - changelog)
  └── README.md (MODIFIED - updated docs)
```

---

## WHAT'S NEW FOR USERS

### On Application Startup
```
Welcome screen with authentication
Username: ________
Password: ________
```

### After Login Success
```
Welcome, [Full Name] ([ROLE])
```

### Menu Changes by Role

**STUDENT**: View events/activities, register, see results
**FACULTY**: Advise associations, view participants
**ASSOCIATION_MEMBER**: Create/manage events and activities
**ADMIN**: Full access to all features

### Logout Option
Each menu includes logout option to return to login screen

---

## SUPPORT DOCUMENTS

**Read these for more info:**

1. **LOGIN_SETUP.md** (120+ lines)
   - Complete setup guide
   - User creation instructions
   - Password hashing guide
   - Role-based access table
   - Security best practices
   - Troubleshooting

2. **IMPLEMENTATION_SUMMARY.md** (200+ lines)
   - Technical implementation details
   - File descriptions
   - Security features
   - Testing scenarios

3. **CHANGES.md** (350+ lines)
   - Detailed changelog
   - All files modified
   - Access control matrix
   - Migration path

---

## VERSION & COMPATIBILITY

✓ Java: 17, 18, 19, 20, 21+
✓ MySQL: 5.7+ (unchanged)
✓ Breaking Changes: NONE
✓ Database Changes: NONE
✓ External Dependencies: NONE (BCrypt optional for enhanced security)

---

## VERIFICATION CHECKLIST

Before deploying to production:

- [ ] All 7 new .java files copied to correct packages
- [ ] 5 modified files updated (Main, ConsoleUI, build scripts, README)
- [ ] Build succeeds: `build-windows.bat` or `./build-unix.sh`
- [ ] users.json created with valid users
- [ ] App runs: `run-windows.bat` or `./run-unix.sh`
- [ ] Login prompt appears
- [ ] Valid credentials accepted
- [ ] Invalid credentials rejected
- [ ] Each role sees appropriate menu
- [ ] Permission checks work
- [ ] Existing features still work
- [ ] users.json file permissions set (600/ACL)
- [ ] users.json in .gitignore (protected)

---

## ROLLBACK PLAN (if needed)

1. Keep backup from Step 1 above
2. Restore from backup: `cp -r department-association-java.backup/* department-association-java/`
3. Rebuild: `build-windows.bat` or `./build-unix.sh`
4. System returns to pre-login state

All user data in database is preserved.

---

**Deployment Guide Created**: November 24, 2025
**Implementation**: Role-Based Login System v1.0
