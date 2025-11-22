package com.deptassoc.db;

/**
 * Centralized SQL query constants using exact database and column names.
 */
public class SQLConstants {
    
    // ASSOCIATION queries
    public static final String FIND_ALL_ASSOCIATIONS = 
        "SELECT assoc_id, assoc_name, establishment_year, department_id, description FROM association";
    public static final String FIND_ASSOCIATION_BY_ID = 
        "SELECT assoc_id, assoc_name, establishment_year, department_id, description FROM association WHERE assoc_id = ?";
    public static final String INSERT_ASSOCIATION = 
        "INSERT INTO association (assoc_name, establishment_year, department_id, description) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_ASSOCIATION = 
        "UPDATE association SET assoc_name = ?, establishment_year = ?, department_id = ?, description = ? WHERE assoc_id = ?";
    public static final String DELETE_ASSOCIATION = 
        "DELETE FROM association WHERE assoc_id = ?";
    
    // FACULTY queries
    public static final String FIND_ALL_FACULTY = 
        "SELECT faculty_id, f_name, f_email, f_phone, designation FROM faculty";
    public static final String FIND_FACULTY_BY_ID = 
        "SELECT faculty_id, f_name, f_email, f_phone, designation FROM faculty WHERE faculty_id = ?";
    public static final String INSERT_FACULTY = 
        "INSERT INTO faculty (f_name, f_email, f_phone, designation) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_FACULTY = 
        "UPDATE faculty SET f_name = ?, f_email = ?, f_phone = ?, designation = ? WHERE faculty_id = ?";
    public static final String DELETE_FACULTY = 
        "DELETE FROM faculty WHERE faculty_id = ?";
    
    // STUDENT queries
    public static final String FIND_ALL_STUDENTS = 
        "SELECT student_id, s_name, s_email, phone FROM student";
    public static final String FIND_STUDENT_BY_ID = 
        "SELECT student_id, s_name, s_email, phone FROM student WHERE student_id = ?";
    public static final String INSERT_STUDENT = 
        "INSERT INTO student (s_name, s_email, phone) VALUES (?, ?, ?)";
    public static final String UPDATE_STUDENT = 
        "UPDATE student SET s_name = ?, s_email = ?, phone = ? WHERE student_id = ?";
    public static final String DELETE_STUDENT = 
        "DELETE FROM student WHERE student_id = ?";
    
    // EVENT queries
    public static final String FIND_ALL_EVENTS = 
        "SELECT event_id, assoc_id, event_name, event_date, venue, description, participant_count FROM event";
    public static final String FIND_EVENT_BY_ID = 
        "SELECT event_id, assoc_id, event_name, event_date, venue, description, participant_count FROM event WHERE event_id = ?";
    public static final String FIND_EVENTS_BY_ASSOCIATION = 
        "SELECT event_id, assoc_id, event_name, event_date, venue, description, participant_count FROM event WHERE assoc_id = ?";
    public static final String INSERT_EVENT = 
        "INSERT INTO event (assoc_id, event_name, event_date, venue, description, participant_count) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_EVENT = 
        "UPDATE event SET assoc_id = ?, event_name = ?, event_date = ?, venue = ?, description = ?, participant_count = ? WHERE event_id = ?";
    public static final String DELETE_EVENT = 
        "DELETE FROM event WHERE event_id = ?";
    
    // ACTIVITY queries
    public static final String FIND_ALL_ACTIVITIES = 
        "SELECT activity_id, event_id, activity_name, description, start_time, end_time, participant_count FROM activity";
    public static final String FIND_ACTIVITY_BY_ID = 
        "SELECT activity_id, event_id, activity_name, description, start_time, end_time, participant_count FROM activity WHERE activity_id = ?";
    public static final String FIND_ACTIVITIES_BY_EVENT = 
        "SELECT activity_id, event_id, activity_name, description, start_time, end_time, participant_count FROM activity WHERE event_id = ?";
    public static final String INSERT_ACTIVITY = 
        "INSERT INTO activity (event_id, activity_name, description, start_time, end_time, participant_count) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_ACTIVITY = 
        "UPDATE activity SET event_id = ?, activity_name = ?, description = ?, start_time = ?, end_time = ?, participant_count = ? WHERE activity_id = ?";
    public static final String DELETE_ACTIVITY = 
        "DELETE FROM activity WHERE activity_id = ?";
    
    // ASSOCIATION_MEMBERS queries
    public static final String FIND_ALL_ASSOCIATION_MEMBERS = 
        "SELECT member_id, assoc_id, student_id, role, joined_date FROM association_members";
    public static final String FIND_ASSOCIATION_MEMBER_BY_ID = 
        "SELECT member_id, assoc_id, student_id, role, joined_date FROM association_members WHERE member_id = ?";
    public static final String FIND_MEMBERS_BY_ASSOCIATION = 
        "SELECT member_id, assoc_id, student_id, role, joined_date FROM association_members WHERE assoc_id = ?";
    public static final String INSERT_ASSOCIATION_MEMBER = 
        "INSERT INTO association_members (assoc_id, student_id, role, joined_date) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_ASSOCIATION_MEMBER = 
        "UPDATE association_members SET assoc_id = ?, student_id = ?, role = ?, joined_date = ? WHERE member_id = ?";
    public static final String DELETE_ASSOCIATION_MEMBER = 
        "DELETE FROM association_members WHERE member_id = ?";
    
    // ASSOCIATION_FACULTY_ADVISERS queries
    public static final String FIND_ALL_ASSOCIATION_FACULTY_ADVISERS = 
        "SELECT adviser_id, assoc_id, faculty_id, role FROM association_faculty_advisers";
    public static final String FIND_ASSOCIATION_FACULTY_ADVISER_BY_ID = 
        "SELECT adviser_id, assoc_id, faculty_id, role FROM association_faculty_advisers WHERE adviser_id = ?";
    public static final String FIND_ADVISERS_BY_ASSOCIATION = 
        "SELECT adviser_id, assoc_id, faculty_id, role FROM association_faculty_advisers WHERE assoc_id = ?";
    public static final String INSERT_ASSOCIATION_FACULTY_ADVISER = 
        "INSERT INTO association_faculty_advisers (assoc_id, faculty_id, role) VALUES (?, ?, ?)";
    public static final String UPDATE_ASSOCIATION_FACULTY_ADVISER = 
        "UPDATE association_faculty_advisers SET assoc_id = ?, faculty_id = ?, role = ? WHERE adviser_id = ?";
    public static final String DELETE_ASSOCIATION_FACULTY_ADVISER = 
        "DELETE FROM association_faculty_advisers WHERE adviser_id = ?";
    
    // ACTIVITY_PARTICIPANTS queries
    public static final String FIND_ALL_ACTIVITY_PARTICIPANTS = 
        "SELECT participant_id, activity_id, student_id, registered_on FROM activity_participants";
    public static final String FIND_ACTIVITY_PARTICIPANT_BY_ID = 
        "SELECT participant_id, activity_id, student_id, registered_on FROM activity_participants WHERE participant_id = ?";
    public static final String FIND_PARTICIPANTS_BY_ACTIVITY = 
        "SELECT participant_id, activity_id, student_id, registered_on FROM activity_participants WHERE activity_id = ?";
    public static final String INSERT_ACTIVITY_PARTICIPANT = 
        "INSERT INTO activity_participants (activity_id, student_id, registered_on) VALUES (?, ?, ?)";
    public static final String UPDATE_ACTIVITY_PARTICIPANT = 
        "UPDATE activity_participants SET activity_id = ?, student_id = ?, registered_on = ? WHERE participant_id = ?";
    public static final String DELETE_ACTIVITY_PARTICIPANT = 
        "DELETE FROM activity_participants WHERE participant_id = ?";
    
    // ACTIVITY_WINNERS queries
    public static final String FIND_ALL_ACTIVITY_WINNERS = 
        "SELECT winner_id, activity_id, student_id, position FROM activity_winners";
    public static final String FIND_ACTIVITY_WINNER_BY_ID = 
        "SELECT winner_id, activity_id, student_id, position FROM activity_winners WHERE winner_id = ?";
    public static final String FIND_WINNERS_BY_ACTIVITY = 
        "SELECT winner_id, activity_id, student_id, position FROM activity_winners WHERE activity_id = ?";
    public static final String INSERT_ACTIVITY_WINNER = 
        "INSERT INTO activity_winners (activity_id, student_id, position) VALUES (?, ?, ?)";
    public static final String UPDATE_ACTIVITY_WINNER = 
        "UPDATE activity_winners SET activity_id = ?, student_id = ?, position = ? WHERE winner_id = ?";
    public static final String DELETE_ACTIVITY_WINNER = 
        "DELETE FROM activity_winners WHERE winner_id = ?";
}
