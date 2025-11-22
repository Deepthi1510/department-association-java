# Department Association Management System

A pure Java SE application (Java 17+) for managing department associations, students, faculties, events, activities, and participation records using JDBC and MySQL.

## Project Overview

This is a **command-line application** with a menu-driven console UI that allows management of:
- Associations (clubs/groups)
- Faculties (advisors)
- Students (members)
- Events (organized by associations)
- Activities (part of events)
- Participants and Winners tracking

**No Spring Framework, No Maven/Gradle** — Uses plain JDBC with simple bash/batch build scripts.

## Prerequisites

### System Requirements
- **Java 17 or higher** (compatible with Java 21)
- **MySQL 5.7+** with existing database `department_association_v2`
- **Windows** (for batch scripts) or **Unix/Linux/macOS** (for shell scripts)

### Required MySQL Driver
- Download `mysql-connector-java-8.x.x.jar` (or newer) from:
  https://dev.mysql.com/downloads/connector/j/
- Place the JAR file in the `lib/` directory

## Project Structure

```
department-association-java/
├── src/
│   └── com/deptassoc/
│       ├── Main.java                 # Entry point
│       ├── db/
│       │   ├── DBConnectionManager.java
│       │   └── SQLConstants.java
│       ├── dao/                      # Data Access Objects
│       │   ├── AssociationDao.java
│       │   ├── FacultyDao.java
│       │   ├── StudentDao.java
│       │   ├── EventDao.java
│       │   ├── ActivityDao.java
│       │   ├── AssociationMemberDao.java
│       │   ├── AssociationFacultyAdviserDao.java
│       │   ├── ActivityParticipantDao.java
│       │   └── ActivityWinnerDao.java
│       ├── model/                    # POJOs
│       │   ├── Association.java
│       │   ├── Faculty.java
│       │   ├── Student.java
│       │   ├── Event.java
│       │   ├── Activity.java
│       │   ├── AssociationMember.java
│       │   ├── AssociationFacultyAdviser.java
│       │   ├── ActivityParticipant.java
│       │   └── ActivityWinner.java
│       └── ui/
│           └── ConsoleUI.java
├── resources/
│   └── config.properties             # Database configuration
├── lib/                              # Place mysql-connector JAR here
├── out/                              # Compiled output (generated)
├── build-windows.bat                 # Windows build script
├── build-unix.sh                     # Unix/Linux/macOS build script
├── run-windows.bat                   # Windows run script
├── run-unix.sh                       # Unix/Linux/macOS run script
├── data-seed.sql                     # Optional sample data
└── README.md                         # This file
```

## Database Schema

The application uses the following exact table names and columns:

### 1. association
```sql
CREATE TABLE association (
    assoc_id INT PRIMARY KEY AUTO_INCREMENT,
    assoc_name VARCHAR(255),
    establishment_year YEAR,
    department_id INT,
    description TEXT
);
```

### 2. faculty
```sql
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    f_name VARCHAR(255),
    f_email VARCHAR(255),
    f_phone VARCHAR(20),
    designation VARCHAR(255)
);
```

### 3. student
```sql
CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    s_name VARCHAR(255),
    s_email VARCHAR(255),
    phone VARCHAR(20)
);
```

### 4. event
```sql
CREATE TABLE event (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    assoc_id INT,
    event_name VARCHAR(255),
    event_date DATE,
    venue VARCHAR(255),
    description TEXT,
    participant_count INT,
    FOREIGN KEY (assoc_id) REFERENCES association(assoc_id)
);
```

### 5. activity
```sql
CREATE TABLE activity (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT,
    activity_name VARCHAR(255),
    description TEXT,
    start_time TIME,
    end_time TIME,
    participant_count INT,
    FOREIGN KEY (event_id) REFERENCES event(event_id)
);
```

### 6. association_members
```sql
CREATE TABLE association_members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    assoc_id INT,
    student_id INT,
    role VARCHAR(255),
    joined_date DATE,
    FOREIGN KEY (assoc_id) REFERENCES association(assoc_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id)
);
```

### 7. association_faculty_advisers
```sql
CREATE TABLE association_faculty_advisers (
    adviser_id INT PRIMARY KEY AUTO_INCREMENT,
    assoc_id INT,
    faculty_id INT,
    role VARCHAR(255),
    FOREIGN KEY (assoc_id) REFERENCES association(assoc_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id)
);
```

### 8. activity_participants
```sql
CREATE TABLE activity_participants (
    participant_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT,
    student_id INT,
    registered_on DATETIME,
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id)
);
```

### 9. activity_winners
```sql
CREATE TABLE activity_winners (
    winner_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT,
    student_id INT,
    position INT,
    FOREIGN KEY (activity_id) REFERENCES activity(activity_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id)
);
```

## Setup Instructions

### Step 1: Database Setup
Ensure your MySQL database `department_association_v2` exists with all required tables. Use the schema above to create them.

### Step 2: Download MySQL JDBC Driver
1. Download `mysql-connector-java-8.x.x.jar` from https://dev.mysql.com/downloads/connector/j/
2. Extract the JAR file
3. Copy it to `lib/` directory (create if missing)

### Step 3: Configure Database Connection
Edit `resources/config.properties`:
```properties
db.host=localhost          # Your MySQL host
db.port=3306               # MySQL port
db.name=department_association_v2  # Your database name
db.user=root               # MySQL username
db.password=your_password  # MySQL password
```

### Step 4: Build the Application

#### Windows
```batch
build-windows.bat
```

#### Unix/Linux/macOS
```bash
chmod +x build-unix.sh
./build-unix.sh
```

The build process:
- Compiles all Java sources to `out/`
- Copies config files to `out/`
- Copies JARs to `out/lib/`
- Creates `out/app.jar` with proper manifest

### Step 5: Run the Application

#### Windows
```batch
run-windows.bat
```

#### Unix/Linux/macOS
```bash
chmod +x run-unix.sh
./run-unix.sh
```

Or directly:
```bash
java -jar out/app.jar
```

## Usage

The application presents a menu-driven console interface:

```
========== MAIN MENU ==========
1. Manage Associations
2. Manage Faculties
3. Manage Students
4. Manage Events
5. Manage Activities
6. Register Participant
7. Add Activity Winner
8. View Reports
9. Exit
==============================
```

### Example Workflows

**Add a New Student:**
1. Select option 3 (Manage Students)
2. Select option 2 (Add New)
3. Enter name, email, phone
4. Return to main menu

**Create an Event:**
1. Select option 4 (Manage Events)
2. Select option 2 (Add New)
3. Enter association ID, event name, date, venue
4. Event created

**Register Participant:**
1. Select option 6 (Register Participant)
2. Enter activity ID and student ID
3. Participant registered automatically with current timestamp

**View Reports:**
1. Select option 8 (View Reports)
2. Choose report type:
   - Participants in Activity
   - Winners of Activity
   - Events by Association
   - Activities by Event
   - Members of Association

## Optional: Load Sample Data

To populate test data:

```bash
mysql -u root -p department_association_v2 < data-seed.sql
```

Then run the application and explore the sample data.

## Code Architecture

### Data Access Layer (DAO)
- Each table has a corresponding DAO class (e.g., `StudentDao`, `EventDao`)
- All DAOs implement:
  - `findAll()` - Retrieve all records
  - `findById(int id)` - Retrieve by primary key
  - `insert(T t)` - Create new record
  - `update(T t)` - Modify record
  - `delete(int id)` - Remove record
- Uses `PreparedStatement` for all queries (prevents SQL injection)
- Try-with-resources for resource cleanup

### Connection Management
- `DBConnectionManager.getConnection()` provides database connections
- Loads configuration from `config.properties` at startup
- Uses MySQL JDBC driver with UTC timezone

### Business Logic
- `ConsoleUI` class handles user interactions
- `SQLConstants` class stores all SQL queries as constants
- Model classes are simple POJOs with getters/setters

## Troubleshooting

### "JDBC Driver Not Found"
**Error:** `Class.forName("com.mysql.cj.jdbc.Driver") failed`
- **Solution:** Ensure `mysql-connector-java-x.x.x.jar` is in `lib/` directory and build script copies it to `out/lib/`

### "Connection Refused"
**Error:** `Communications link failure` or `Connection refused`
- **Solution:** 
  - Verify MySQL server is running
  - Check `db.host`, `db.port` in `config.properties`
  - Verify credentials (username/password)
  - Ping MySQL: `mysql -h localhost -u root -p`

### "config.properties Not Found"
**Error:** `config.properties not found in classpath`
- **Solution:** Run build script to copy config files to `out/` directory

### "Table Not Found"
**Error:** `Table 'database.table_name' doesn't exist`
- **Solution:** 
  - Create tables using schema provided above
  - Verify table names exactly match (case-sensitive)
  - Verify database name is `department_association_v2`

### "Access Denied"
**Error:** `Access denied for user 'root'@'localhost'`
- **Solution:** Update username and password in `config.properties`

### Build Errors on Windows
**Error:** `javac: command not found`
- **Solution:** 
  - Ensure Java is installed: `java -version`
  - Add Java `bin/` directory to PATH
  - Restart command prompt after PATH changes

## Compilation Details

**Target Java Version:** Java 17+
**Source Encoding:** UTF-8
**JAR Manifest:**
```
Manifest-Version: 1.0
Main-Class: com.deptassoc.Main
Class-Path: lib/*
```

## Performance Considerations

- Each DAO method creates a new database connection (acceptable for this scale)
- For production, consider implementing connection pooling (HikariCP)
- SQL queries are parameterized to prevent injection
- All ResultSet resources are closed via try-with-resources

## Security Notes

- All SQL uses `PreparedStatement` with parameter binding
- No hardcoded credentials (loaded from config.properties)
- No DDL/schema modification from application
- Follows principle of least privilege

## Limitations & Future Enhancements

- **Current:** Console UI only (Swing GUI skeleton provided)
- **No:** Connection pooling, caching, transaction management
- **Possible enhancements:**
  - Swing GUI implementation
  - Connection pooling (HikariCP)
  - Advanced search/filtering
  - Batch operations
  - Report generation (PDF/Excel)
  - Multi-user session management

## Technology Stack

- **Language:** Java 17+ (compatible with Java 21)
- **Database:** MySQL 5.7+
- **JDBC Driver:** mysql-connector-java-8.x+
- **Build:** Native javac (no Maven/Gradle)
- **UI:** Console (stdin/stdout)

## License & Attribution

This project is provided as-is for educational and departmental use.

## Support & Contact

For issues or questions:
1. Check **Troubleshooting** section above
2. Verify all prerequisites are met
3. Review error messages and stack traces
4. Check database connectivity with: `mysql -h [host] -u [user] -p [database]`

---

**Last Updated:** November 2025
**Java Compatibility:** 17, 18, 19, 20, 21+
**MySQL Compatibility:** 5.7, 8.0+
