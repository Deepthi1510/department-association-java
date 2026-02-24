package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.Faculty;
import com.deptassoc.dto.ActivityDTO;
import com.deptassoc.dto.EventDTO;
import com.deptassoc.dto.StudentDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Faculty entity.
 */
public class FacultyDao {
    
    public List<Faculty> findAll() throws SQLException {
        List<Faculty> faculties = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_FACULTY)) {
            while (rs.next()) {
                faculties.add(mapRowToFaculty(rs));
            }
        }
        return faculties;
    }
    
    public Faculty findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_FACULTY_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFaculty(rs);
                }
            }
        }
        return null;
    }
    
    public void insert(Faculty faculty) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_FACULTY)) {
            stmt.setString(1, faculty.getFName());
            stmt.setString(2, faculty.getFEmail());
            stmt.setString(3, faculty.getFPhone());
            stmt.setString(4, faculty.getDesignation());
            stmt.executeUpdate();
        }
    }
    
    public void update(Faculty faculty) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_FACULTY)) {
            stmt.setString(1, faculty.getFName());
            stmt.setString(2, faculty.getFEmail());
            stmt.setString(3, faculty.getFPhone());
            stmt.setString(4, faculty.getDesignation());
            stmt.setInt(5, faculty.getFacultyId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_FACULTY)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Faculty mapRowToFaculty(ResultSet rs) throws SQLException {
        return new Faculty(
            rs.getInt("faculty_id"),
            rs.getString("f_name"),
            rs.getString("f_email"),
            rs.getString("f_phone"),
            rs.getString("designation")
        );
    }

    /**
     * Get all events assigned to a faculty member.
     * Faculty is assigned to an event if they are listed in association_faculty_advisers
     * and the event belongs to the same association.
     * 
     * @param facultyId the faculty ID
     * @return List of EventDTO objects
     * @throws SQLException if database error occurs
     */
    public List<EventDTO> getEventsForFaculty(int facultyId) throws SQLException {
        List<EventDTO> events = new ArrayList<>();
        String sql = "SELECT e.event_id, e.event_name, e.event_date, e.venue, e.description " +
                     "FROM event e " +
                     "JOIN association_faculty_advisers afa ON e.assoc_id = afa.assoc_id " +
                     "WHERE afa.faculty_id = ? " +
                     "ORDER BY e.event_date";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, facultyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EventDTO dto = new EventDTO(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getDate("event_date"),
                        rs.getString("venue"),
                        rs.getString("description")
                    );
                    events.add(dto);
                }
            }
        }
        
        return events;
    }

    /**
     * Get all activities for a specific event.
     * 
     * @param eventId the event ID
     * @return List of ActivityDTO objects
     * @throws SQLException if database error occurs
     */
    public List<ActivityDTO> getActivitiesForEvent(int eventId) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String sql = "SELECT activity_id, activity_name, description, start_time, end_time, participant_count " +
                     "FROM activity " +
                     "WHERE event_id = ? " +
                     "ORDER BY start_time";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActivityDTO dto = new ActivityDTO(
                        rs.getInt("activity_id"),
                        rs.getString("activity_name"),
                        rs.getString("description"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getInt("participant_count")
                    );
                    dto.setEventId(eventId);
                    activities.add(dto);
                }
            }
        }
        
        return activities;
    }

    /**
     * Get all students registered for a specific activity.
     * 
     * @param activityId the activity ID
     * @return List of StudentDTO objects with registration info
     * @throws SQLException if database error occurs
     */
    public List<StudentDTO> getParticipantsForActivity(int activityId) throws SQLException {
        List<StudentDTO> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.s_name, s.s_email, s.phone, ap.registered_on " +
                     "FROM activity_participants ap " +
                     "JOIN student s ON ap.student_id = s.student_id " +
                     "WHERE ap.activity_id = ? " +
                     "ORDER BY ap.registered_on DESC";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentDTO dto = new StudentDTO(
                        rs.getInt("student_id"),
                        rs.getString("s_name"),
                        rs.getString("s_email"),
                        rs.getString("phone")
                    );
                    dto.setRegisteredOn(rs.getTimestamp("registered_on"));
                    students.add(dto);
                }
            }
        }
        
        return students;
    }

    /**
     * Add a new activity to an event.
     * 
     * @param activityDTO the activity to add
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean addActivity(ActivityDTO activityDTO) throws SQLException {
        String sql = "INSERT INTO activity (event_id, activity_name, description, start_time, end_time, participant_count) " +
                     "VALUES (?, ?, ?, ?, ?, 0)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityDTO.getEventId());
            stmt.setString(2, activityDTO.getActivityName());
            stmt.setString(3, activityDTO.getDescription());
            stmt.setTime(4, activityDTO.getStartTime());
            stmt.setTime(5, activityDTO.getEndTime());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Delete an activity from the database.
     * 
     * @param activityId the activity ID to delete
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs (including FK violations)
     */
    public boolean deleteActivity(int activityId) throws SQLException {
        String sql = "DELETE FROM activity WHERE activity_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
