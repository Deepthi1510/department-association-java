package com.deptassoc.ui;

import com.deptassoc.dao.*;
import com.deptassoc.model.*;
import com.deptassoc.auth.AuthResult;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 * CHANGED: Console-based UI for managing department associations.
 * Now role-aware with restricted menus based on user role.
 */
public class ConsoleUI {
    
    private Scanner scanner;
    private AssociationDao assocDao;
    private FacultyDao facultyDao;
    private StudentDao studentDao;
    private EventDao eventDao;
    private ActivityDao activityDao;
    private AssociationMemberDao memberDao;
    private AssociationFacultyAdviserDao adviserDao;
    private ActivityParticipantDao participantDao;
    private ActivityWinnerDao winnerDao;
    
    private AuthResult authResult;  // CHANGED: Added for role-based access
    
    // CHANGED: Updated constructor to accept AuthResult
    public ConsoleUI(AuthResult authResult) {
        this.scanner = new Scanner(System.in);
        this.assocDao = new AssociationDao();
        this.facultyDao = new FacultyDao();
        this.studentDao = new StudentDao();
        this.eventDao = new EventDao();
        this.activityDao = new ActivityDao();
        this.memberDao = new AssociationMemberDao();
        this.adviserDao = new AssociationFacultyAdviserDao();
        this.participantDao = new ActivityParticipantDao();
        this.winnerDao = new ActivityWinnerDao();
        this.authResult = authResult;
    }
    
    // CHANGED: Overload for backward compatibility
    public ConsoleUI() {
        this(null);
    }
    
    /**
     * CHANGED: Starts the main menu loop with role-based access control.
     */
    public void start() {
        boolean running = true;
        while (running) {
            try {
                printMainMenu();
                int choice = readInt("Enter your choice: ");
                
                switch (choice) {
                    case 1:
                        if (canManageAssociations()) manageAssociations();
                        else System.out.println("❌ You don't have permission to manage associations.");
                        break;
                    case 2:
                        if (canManageFaculties()) manageFaculties();
                        else System.out.println("❌ You don't have permission to manage faculties.");
                        break;
                    case 3:
                        if (canManageStudents()) manageStudents();
                        else System.out.println("❌ You don't have permission to manage students.");
                        break;
                    case 4:
                        if (canManageEvents()) manageEvents();
                        else System.out.println("❌ You don't have permission to manage events.");
                        break;
                    case 5:
                        if (canManageActivities()) manageActivities();
                        else System.out.println("❌ You don't have permission to manage activities.");
                        break;
                    case 6:
                        if (canRegisterParticipant()) registerParticipant();
                        else System.out.println("❌ You don't have permission to register participants.");
                        break;
                    case 7:
                        if (canAddWinner()) addWinner();
                        else System.out.println("❌ You don't have permission to add winners.");
                        break;
                    case 8:
                        showReports();  // Reports available to all
                        break;
                    case 9:
                        running = false;
                        System.out.println("\nGoodbye " + (authResult != null ? authResult.getDisplayName() : "User") + "!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * CHANGED: Role-based menu display.
     */
    private void printMainMenu() {
        String role = authResult != null ? authResult.getRole() : "NONE";
        System.out.println("\n========== MAIN MENU (" + role + ") ==========");
        
        if ("STUDENT".equals(role)) {
            printStudentMenu();
        } else if ("FACULTY".equals(role)) {
            printFacultyMenu();
        } else if ("ASSOCIATION_MEMBER".equals(role)) {
            printAssociationMemberMenu();
        } else {
            printAdminMenu();  // Default/Admin view
        }
    }
    
    /**
     * Student-restricted menu.
     */
    private void printStudentMenu() {
        System.out.println("1. View Events (Read-only)");
        System.out.println("2. View Activities (Read-only)");
        System.out.println("3. Register for Activity");
        System.out.println("4. View My Participation Results");
        System.out.println("5. View Winners");
        System.out.println("6. View Reports");
        System.out.println("7. Logout");
    }
    
    /**
     * Faculty-restricted menu.
     */
    private void printFacultyMenu() {
        System.out.println("1. View Associations (Advise)");
        System.out.println("2. View Faculties (Read-only)");
        System.out.println("3. View Students (Read-only)");
        System.out.println("4. View Events (Advise)");
        System.out.println("5. View Activities (Advise)");
        System.out.println("6. View Participants");
        System.out.println("7. View Reports");
        System.out.println("8. Logout");
    }
    
    /**
     * Association member menu.
     */
    private void printAssociationMemberMenu() {
        System.out.println("1. Manage Associations");
        System.out.println("2. Manage Events");
        System.out.println("3. Manage Activities");
        System.out.println("4. Register Participant");
        System.out.println("5. Add Activity Winner");
        System.out.println("6. View Reports");
        System.out.println("7. Logout");
    }
    
    /**
     * Admin menu (full access).
     */
    private void printAdminMenu() {
        System.out.println("1. Manage Associations");
        System.out.println("2. Manage Faculties");
        System.out.println("3. Manage Students");
        System.out.println("4. Manage Events");
        System.out.println("5. Manage Activities");
        System.out.println("6. Register Participant");
        System.out.println("7. Add Activity Winner");
        System.out.println("8. View Reports");
        System.out.println("9. Logout");
    }
    
    // ===== ROLE-BASED ACCESS CONTROL =====
    
    private boolean canManageAssociations() {
        return authResult == null || 
               "ASSOCIATION_MEMBER".equals(authResult.getRole()) ||
               authResult.getRole() == null;  // Admin
    }
    
    private boolean canManageFaculties() {
        return authResult == null || 
               authResult.getRole() == null;  // Admin only
    }
    
    private boolean canManageStudents() {
        return authResult == null || 
               authResult.getRole() == null;  // Admin only
    }
    
    private boolean canManageEvents() {
        return authResult == null || 
               "ASSOCIATION_MEMBER".equals(authResult.getRole()) ||
               "FACULTY".equals(authResult.getRole()) ||
               authResult.getRole() == null;  // Admin
    }
    
    private boolean canManageActivities() {
        return authResult == null || 
               "ASSOCIATION_MEMBER".equals(authResult.getRole()) ||
               authResult.getRole() == null;  // Admin
    }
    
    private boolean canRegisterParticipant() {
        return authResult == null || 
               "STUDENT".equals(authResult.getRole()) ||
               "ASSOCIATION_MEMBER".equals(authResult.getRole()) ||
               authResult.getRole() == null;  // Admin
    }
    
    private boolean canAddWinner() {
        return authResult == null || 
               "ASSOCIATION_MEMBER".equals(authResult.getRole()) ||
               authResult.getRole() == null;  // Admin
    }
    
    // ===== ASSOCIATION MANAGEMENT =====
    private void manageAssociations() throws Exception {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Association Management ---");
            System.out.println("1. List All");
            System.out.println("2. Add New");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Back");
            
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    listAssociations();
                    break;
                case 2:
                    addAssociation();
                    break;
                case 3:
                    updateAssociation();
                    break;
                case 4:
                    deleteAssociation();
                    break;
                case 5:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void listAssociations() throws Exception {
        List<Association> associations = assocDao.findAll();
        if (associations.isEmpty()) {
            System.out.println("No associations found.");
        } else {
            System.out.println("\n--- All Associations ---");
            for (Association a : associations) {
                System.out.printf("ID: %d | Name: %s | Year: %d | Dept: %d | Desc: %s%n",
                    a.getAssocId(), a.getAssocName(), a.getEstablishmentYear(),
                    a.getDepartmentId(), a.getDescription());
            }
        }
    }
    
    private void addAssociation() throws Exception {
        System.out.println("\n--- Add New Association ---");
        String name = readString("Association Name: ");
        int year = readInt("Establishment Year (YYYY): ");
        int deptId = readInt("Department ID: ");
        String desc = readString("Description: ");
        
        Association assoc = new Association(0, name, year, deptId, desc);
        assocDao.insert(assoc);
        System.out.println("Association added successfully!");
    }
    
    private void updateAssociation() throws Exception {
        System.out.println("\n--- Update Association ---");
        int id = readInt("Enter Association ID to update: ");
        Association assoc = assocDao.findById(id);
        if (assoc == null) {
            System.out.println("Association not found.");
            return;
        }
        
        String name = readString("New Name (current: " + assoc.getAssocName() + "): ");
        int year = readInt("New Year (current: " + assoc.getEstablishmentYear() + "): ");
        int deptId = readInt("New Department ID (current: " + assoc.getDepartmentId() + "): ");
        String desc = readString("New Description (current: " + assoc.getDescription() + "): ");
        
        assoc.setAssocName(name.isEmpty() ? assoc.getAssocName() : name);
        assoc.setEstablishmentYear(year == 0 ? assoc.getEstablishmentYear() : year);
        assoc.setDepartmentId(deptId == 0 ? assoc.getDepartmentId() : deptId);
        assoc.setDescription(desc.isEmpty() ? assoc.getDescription() : desc);
        
        assocDao.update(assoc);
        System.out.println("Association updated successfully!");
    }
    
    private void deleteAssociation() throws Exception {
        System.out.println("\n--- Delete Association ---");
        int id = readInt("Enter Association ID to delete: ");
        assocDao.delete(id);
        System.out.println("Association deleted successfully!");
    }
    
    // ===== FACULTY MANAGEMENT =====
    private void manageFaculties() throws Exception {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Faculty Management ---");
            System.out.println("1. List All");
            System.out.println("2. Add New");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Back");
            
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    listFaculties();
                    break;
                case 2:
                    addFaculty();
                    break;
                case 3:
                    updateFaculty();
                    break;
                case 4:
                    deleteFaculty();
                    break;
                case 5:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void listFaculties() throws Exception {
        List<Faculty> faculties = facultyDao.findAll();
        if (faculties.isEmpty()) {
            System.out.println("No faculties found.");
        } else {
            System.out.println("\n--- All Faculties ---");
            for (Faculty f : faculties) {
                System.out.printf("ID: %d | Name: %s | Email: %s | Phone: %s | Desig: %s%n",
                    f.getFacultyId(), f.getFName(), f.getFEmail(), f.getFPhone(), f.getDesignation());
            }
        }
    }
    
    private void addFaculty() throws Exception {
        System.out.println("\n--- Add New Faculty ---");
        String name = readString("Name: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        String designation = readString("Designation: ");
        
        Faculty faculty = new Faculty(0, name, email, phone, designation);
        facultyDao.insert(faculty);
        System.out.println("Faculty added successfully!");
    }
    
    private void updateFaculty() throws Exception {
        System.out.println("\n--- Update Faculty ---");
        int id = readInt("Enter Faculty ID to update: ");
        Faculty faculty = facultyDao.findById(id);
        if (faculty == null) {
            System.out.println("Faculty not found.");
            return;
        }
        
        String name = readString("New Name (current: " + faculty.getFName() + "): ");
        String email = readString("New Email (current: " + faculty.getFEmail() + "): ");
        String phone = readString("New Phone (current: " + faculty.getFPhone() + "): ");
        String designation = readString("New Designation (current: " + faculty.getDesignation() + "): ");
        
        faculty.setFName(name.isEmpty() ? faculty.getFName() : name);
        faculty.setFEmail(email.isEmpty() ? faculty.getFEmail() : email);
        faculty.setFPhone(phone.isEmpty() ? faculty.getFPhone() : phone);
        faculty.setDesignation(designation.isEmpty() ? faculty.getDesignation() : designation);
        
        facultyDao.update(faculty);
        System.out.println("Faculty updated successfully!");
    }
    
    private void deleteFaculty() throws Exception {
        System.out.println("\n--- Delete Faculty ---");
        int id = readInt("Enter Faculty ID to delete: ");
        facultyDao.delete(id);
        System.out.println("Faculty deleted successfully!");
    }
    
    // ===== STUDENT MANAGEMENT =====
    private void manageStudents() throws Exception {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. List All");
            System.out.println("2. Add New");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Back");
            
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    listStudents();
                    break;
                case 2:
                    addStudent();
                    break;
                case 3:
                    updateStudent();
                    break;
                case 4:
                    deleteStudent();
                    break;
                case 5:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void listStudents() throws Exception {
        List<Student> students = studentDao.findAll();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.println("\n--- All Students ---");
            for (Student s : students) {
                System.out.printf("ID: %d | Name: %s | Email: %s | Phone: %s%n",
                    s.getStudentId(), s.getSName(), s.getSEmail(), s.getPhone());
            }
        }
    }
    
    private void addStudent() throws Exception {
        System.out.println("\n--- Add New Student ---");
        String name = readString("Name: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        
        Student student = new Student(0, name, email, phone);
        studentDao.insert(student);
        System.out.println("Student added successfully!");
    }
    
    private void updateStudent() throws Exception {
        System.out.println("\n--- Update Student ---");
        int id = readInt("Enter Student ID to update: ");
        Student student = studentDao.findById(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        String name = readString("New Name (current: " + student.getSName() + "): ");
        String email = readString("New Email (current: " + student.getSEmail() + "): ");
        String phone = readString("New Phone (current: " + student.getPhone() + "): ");
        
        student.setSName(name.isEmpty() ? student.getSName() : name);
        student.setSEmail(email.isEmpty() ? student.getSEmail() : email);
        student.setPhone(phone.isEmpty() ? student.getPhone() : phone);
        
        studentDao.update(student);
        System.out.println("Student updated successfully!");
    }
    
    private void deleteStudent() throws Exception {
        System.out.println("\n--- Delete Student ---");
        int id = readInt("Enter Student ID to delete: ");
        studentDao.delete(id);
        System.out.println("Student deleted successfully!");
    }
    
    // ===== EVENT MANAGEMENT =====
    private void manageEvents() throws Exception {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Event Management ---");
            System.out.println("1. List All");
            System.out.println("2. Add New");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Back");
            
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    listEvents();
                    break;
                case 2:
                    addEvent();
                    break;
                case 3:
                    updateEvent();
                    break;
                case 4:
                    deleteEvent();
                    break;
                case 5:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void listEvents() throws Exception {
        List<Event> events = eventDao.findAll();
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            System.out.println("\n--- All Events ---");
            for (Event e : events) {
                System.out.printf("ID: %d | AssocID: %d | Name: %s | Date: %s | Venue: %s | Count: %d%n",
                    e.getEventId(), e.getAssocId(), e.getEventName(), e.getEventDate(),
                    e.getVenue(), e.getParticipantCount());
            }
        }
    }
    
    private void addEvent() throws Exception {
        System.out.println("\n--- Add New Event ---");
        int assocId = readInt("Association ID: ");
        String name = readString("Event Name: ");
        String dateStr = readString("Event Date (YYYY-MM-DD): ");
        String venue = readString("Venue: ");
        String desc = readString("Description: ");
        int count = readInt("Participant Count: ");
        
        Date date = Date.valueOf(dateStr);
        Event event = new Event(0, assocId, name, date, venue, desc, count);
        eventDao.insert(event);
        System.out.println("Event added successfully!");
    }
    
    private void updateEvent() throws Exception {
        System.out.println("\n--- Update Event ---");
        int id = readInt("Enter Event ID to update: ");
        Event event = eventDao.findById(id);
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }
        
        System.out.println("Event found: " + event.getEventName());
        String name = readString("New Name (press Enter to keep): ");
        if (!name.isEmpty()) event.setEventName(name);
        
        int count = readInt("New Participant Count (0 to keep): ");
        if (count > 0) event.setParticipantCount(count);
        
        eventDao.update(event);
        System.out.println("Event updated successfully!");
    }
    
    private void deleteEvent() throws Exception {
        System.out.println("\n--- Delete Event ---");
        int id = readInt("Enter Event ID to delete: ");
        eventDao.delete(id);
        System.out.println("Event deleted successfully!");
    }
    
    // ===== ACTIVITY MANAGEMENT =====
    private void manageActivities() throws Exception {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Activity Management ---");
            System.out.println("1. List All");
            System.out.println("2. Add New");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Back");
            
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    listActivities();
                    break;
                case 2:
                    addActivity();
                    break;
                case 3:
                    updateActivity();
                    break;
                case 4:
                    deleteActivity();
                    break;
                case 5:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void listActivities() throws Exception {
        List<Activity> activities = activityDao.findAll();
        if (activities.isEmpty()) {
            System.out.println("No activities found.");
        } else {
            System.out.println("\n--- All Activities ---");
            for (Activity a : activities) {
                System.out.printf("ID: %d | EventID: %d | Name: %s | Start: %s | End: %s | Count: %d%n",
                    a.getActivityId(), a.getEventId(), a.getActivityName(),
                    a.getStartTime(), a.getEndTime(), a.getParticipantCount());
            }
        }
    }
    
    private void addActivity() throws Exception {
        System.out.println("\n--- Add New Activity ---");
        int eventId = readInt("Event ID: ");
        String name = readString("Activity Name: ");
        String desc = readString("Description: ");
        String startStr = readString("Start Time (HH:MM:SS): ");
        String endStr = readString("End Time (HH:MM:SS): ");
        int count = readInt("Participant Count: ");
        
        Time startTime = Time.valueOf(startStr);
        Time endTime = Time.valueOf(endStr);
        Activity activity = new Activity(0, eventId, name, desc, startTime, endTime, count);
        activityDao.insert(activity);
        System.out.println("Activity added successfully!");
    }
    
    private void updateActivity() throws Exception {
        System.out.println("\n--- Update Activity ---");
        int id = readInt("Enter Activity ID to update: ");
        Activity activity = activityDao.findById(id);
        if (activity == null) {
            System.out.println("Activity not found.");
            return;
        }
        
        String name = readString("New Name (press Enter to keep): ");
        if (!name.isEmpty()) activity.setActivityName(name);
        
        int count = readInt("New Participant Count (0 to keep): ");
        if (count > 0) activity.setParticipantCount(count);
        
        activityDao.update(activity);
        System.out.println("Activity updated successfully!");
    }
    
    private void deleteActivity() throws Exception {
        System.out.println("\n--- Delete Activity ---");
        int id = readInt("Enter Activity ID to delete: ");
        activityDao.delete(id);
        System.out.println("Activity deleted successfully!");
    }
    
    // ===== REGISTER PARTICIPANT =====
    private void registerParticipant() throws Exception {
        System.out.println("\n--- Register Participant for Activity ---");
        int activityId = readInt("Enter Activity ID: ");
        int studentId = readInt("Enter Student ID: ");
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ActivityParticipant participant = new ActivityParticipant(0, activityId, studentId, timestamp);
        participantDao.insert(participant);
        System.out.println("Participant registered successfully!");
    }
    
    // ===== ADD WINNER =====
    private void addWinner() throws Exception {
        System.out.println("\n--- Add Activity Winner ---");
        int activityId = readInt("Enter Activity ID: ");
        int studentId = readInt("Enter Student ID: ");
        int position = readInt("Position (1st, 2nd, 3rd...): ");
        
        ActivityWinner winner = new ActivityWinner(0, activityId, studentId, position);
        winnerDao.insert(winner);
        System.out.println("Winner added successfully!");
    }
    
    // ===== REPORTS =====
    private void showReports() throws Exception {
        boolean viewing = true;
        while (viewing) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Participants in Activity");
            System.out.println("2. Winners of Activity");
            System.out.println("3. Events by Association");
            System.out.println("4. Activities by Event");
            System.out.println("5. Members of Association");
            System.out.println("6. Back");
            
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    reportParticipantsInActivity();
                    break;
                case 2:
                    reportWinnersOfActivity();
                    break;
                case 3:
                    reportEventsByAssociation();
                    break;
                case 4:
                    reportActivitiesByEvent();
                    break;
                case 5:
                    reportMembersOfAssociation();
                    break;
                case 6:
                    viewing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void reportParticipantsInActivity() throws Exception {
        int activityId = readInt("Enter Activity ID: ");
        Activity activity = activityDao.findById(activityId);
        if (activity == null) {
            System.out.println("Activity not found.");
            return;
        }
        
        List<ActivityParticipant> participants = participantDao.findByActivity(activityId);
        System.out.println("\n--- Participants in " + activity.getActivityName() + " ---");
        if (participants.isEmpty()) {
            System.out.println("No participants.");
        } else {
            for (ActivityParticipant p : participants) {
                Student s = studentDao.findById(p.getStudentId());
                System.out.printf("Student: %s (ID: %d) | Registered: %s%n",
                    s.getSName(), s.getStudentId(), p.getRegisteredOn());
            }
        }
    }
    
    private void reportWinnersOfActivity() throws Exception {
        int activityId = readInt("Enter Activity ID: ");
        Activity activity = activityDao.findById(activityId);
        if (activity == null) {
            System.out.println("Activity not found.");
            return;
        }
        
        List<ActivityWinner> winners = winnerDao.findByActivity(activityId);
        System.out.println("\n--- Winners of " + activity.getActivityName() + " ---");
        if (winners.isEmpty()) {
            System.out.println("No winners.");
        } else {
            for (ActivityWinner w : winners) {
                Student s = studentDao.findById(w.getStudentId());
                System.out.printf("Position %d: %s (ID: %d)%n",
                    w.getPosition(), s.getSName(), s.getStudentId());
            }
        }
    }
    
    private void reportEventsByAssociation() throws Exception {
        int assocId = readInt("Enter Association ID: ");
        Association assoc = assocDao.findById(assocId);
        if (assoc == null) {
            System.out.println("Association not found.");
            return;
        }
        
        List<Event> events = eventDao.findByAssociation(assocId);
        System.out.println("\n--- Events of " + assoc.getAssocName() + " ---");
        if (events.isEmpty()) {
            System.out.println("No events.");
        } else {
            for (Event e : events) {
                System.out.printf("Event: %s | Date: %s | Venue: %s | Participants: %d%n",
                    e.getEventName(), e.getEventDate(), e.getVenue(), e.getParticipantCount());
            }
        }
    }
    
    private void reportActivitiesByEvent() throws Exception {
        int eventId = readInt("Enter Event ID: ");
        Event event = eventDao.findById(eventId);
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }
        
        List<Activity> activities = activityDao.findByEvent(eventId);
        System.out.println("\n--- Activities in " + event.getEventName() + " ---");
        if (activities.isEmpty()) {
            System.out.println("No activities.");
        } else {
            for (Activity a : activities) {
                System.out.printf("Activity: %s | Time: %s - %s | Participants: %d%n",
                    a.getActivityName(), a.getStartTime(), a.getEndTime(), a.getParticipantCount());
            }
        }
    }
    
    private void reportMembersOfAssociation() throws Exception {
        int assocId = readInt("Enter Association ID: ");
        Association assoc = assocDao.findById(assocId);
        if (assoc == null) {
            System.out.println("Association not found.");
            return;
        }
        
        List<AssociationMember> members = memberDao.findByAssociation(assocId);
        System.out.println("\n--- Members of " + assoc.getAssocName() + " ---");
        if (members.isEmpty()) {
            System.out.println("No members.");
        } else {
            for (AssociationMember m : members) {
                Student s = studentDao.findById(m.getStudentId());
                System.out.printf("Member: %s (ID: %d) | Role: %s | Joined: %s%n",
                    s.getSName(), s.getStudentId(), m.getRole(), m.getJoinedDate());
            }
        }
    }
    
    // ===== UTILITY METHODS =====
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Using 0.");
            return 0;
        }
    }
}
