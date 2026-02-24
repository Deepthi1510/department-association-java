# QUICK START - Role-Based Login System

## 60-Second Setup

### 1. Build (30 seconds)
```bash
# Windows
build-windows.bat

# Unix/Linux/macOS
./build-unix.sh
```

### 2. Create Users (20 seconds)
```bash
java -cp out com.deptassoc.util.SetupUtil init-users
```
Creates `users.json` with sample users

### 3. Run (10 seconds)
```bash
# Windows
run-windows.bat

# Unix/Linux/macOS
./run-unix.sh
```

---

## Test Credentials (After init-users)

| Username | Password | Role |
|----------|----------|------|
| alice | password123 | STUDENT |
| bob | password123 | STUDENT |
| drsmith | password123 | FACULTY |
| ashok | password123 | ASSOCIATION_MEMBER |

⚠️ **Change passwords after first login!**

---

## Common Commands

### Generate Password Hash
```bash
java -cp out com.deptassoc.util.SetupUtil hash "mypassword"
# Output: salt:hash (copy into users.json)
```

### Create Users Interactively
Edit `users.json` manually:
```json
{
  "type": "STUDENT",
  "id": 1,
  "username": "yourname",
  "passwordHash": "paste-hash-here"
}
```

### Protect users.json (Security)
```bash
# Unix/Linux
chmod 600 users.json

# Windows (as admin)
icacls users.json /inheritance:r /grant:r "%USERNAME%:F"
```

---

## What's New

✓ Login screen on startup
✓ Role-based menu restrictions
✓ Password hashing with salt
✓ 5-attempt login limit
✓ Logout option in menu

---

## Need Help?

**For detailed setup**: See `LOGIN_SETUP.md`
**For technical details**: See `IMPLEMENTATION_SUMMARY.md`
**For all changes**: See `CHANGES.md`
**For deployment**: See `DEPLOYMENT.md`

---

## Login Flow

```
App Start
   ↓
Username/Password Prompt
   ↓
Verify Against users.json
   ↓
SUCCESS → Role-Based Menu
   ↓
FAILURE → Retry (max 5 attempts)
```

---

## Role-Specific Menus

**STUDENT** (7 options):
- View Events
- View Activities
- Register for Activity
- View Results
- View Winners
- View Reports
- Logout

**FACULTY** (8 options):
- View Associations
- View Faculties
- View Students
- View Events
- View Activities
- View Participants
- View Reports
- Logout

**ASSOCIATION_MEMBER** (7 options):
- Manage Associations
- Manage Events
- Manage Activities
- Register Participant
- Add Winner
- View Reports
- Logout

**ADMIN** (Full Access):
- All 8+ management features
- No restrictions

---

## Verify Installation

After `build-windows.bat` or `./build-unix.sh`:

```
out/
├── app.jar ✓
├── config.properties ✓
├── lib/
│   └── mysql-connector-j-*.jar ✓
└── com/
    └── deptassoc/
        ├── auth/ ✓ (NEW)
        ├── dao/
        ├── db/
        ├── model/
        ├── ui/
        └── util/ ✓ (NEW)
```

After `java -cp out com.deptassoc.util.SetupUtil init-users`:

```
users.json ✓ (created)
```

---

## First Run Checklist

- [ ] Build succeeds
- [ ] `users.json` created
- [ ] App starts with login prompt
- [ ] Login with sample credentials works
- [ ] Role-specific menu appears
- [ ] Can perform role operations
- [ ] Logout returns to login

---

## Troubleshooting (Common Issues)

| Error | Solution |
|-------|----------|
| "No users configured" | Run `SetupUtil init-users` or create users.json |
| "Invalid credentials" | Check username/password spelling in users.json |
| Build error | Ensure Java 17+ is installed: `java -version` |
| Permission denied on users.json | Run `chmod 600 users.json` (Unix) |
| 5 attempts exceeded | Restart the application |

---

## Next Steps

1. ✓ Build: `build-windows.bat` or `./build-unix.sh`
2. ✓ Setup: `java -cp out com.deptassoc.util.SetupUtil init-users`
3. ✓ Run: `run-windows.bat` or `./run-unix.sh`
4. ✓ Login: Use credentials from users.json
5. ✓ Explore: Role-based menu
6. → Change passwords (recommended)
7. → Add more users (edit users.json or use SetupUtil)
8. → Protect users.json (chmod 600 or NTFS ACL)

---

## Security Reminders

🔒 **Protect users.json**
- Set file permissions to 600 (Unix) or ACL (Windows)
- Never commit to git (in .gitignore)
- Change default passwords

🔒 **Password Management**
- Passwords stored as SHA-256 hashes with salt
- Never plaintext
- Max 5 login attempts per session

🔒 **Database**
- Database credentials in `config.properties`
- No users.json data goes to database
- All operations still use JDBC

---

**Ready to go!** 🚀

Questions? See detailed docs:
- LOGIN_SETUP.md (comprehensive guide)
- DEPLOYMENT.md (deployment steps)
- CHANGES.md (technical details)
