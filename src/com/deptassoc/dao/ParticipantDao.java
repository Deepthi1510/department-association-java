package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.dto.ActivityDTO;
import com.deptassoc.dto.RegistrationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for student activity registrations.
 * Handles queries and DML operations on activity_participants and related tables.
 */
public class ParticipantDao {

    /**
     * Finds all registrations for a given student.
     * 
     * @param studentId the student ID
     * @return List of RegistrationDTO objects
     * @throws SQLException if database error occurs
     */
    public List<RegistrationDTO> findRegistrationsByStudent(int studentId) throws SQLException {
        List<RegistrationDTO> registrations = new ArrayList<>();
        String sql = "SELECT ap.participant_id, ap.activity_id, a.activity_name, e.event_name, ap.registered_on " +
                     "FROM activity_participants ap " +
                     "JOIN activity a ON ap.activity_id = a.activity_id " +
                     "JOIN event e ON a.event_id = e.event_id " +
                     "WHERE ap.student_id = ? " +
                     "ORDER BY ap.registered_on DESC";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RegistrationDTO dto = new RegistrationDTO(
                        rs.getInt("participant_id"),
                        rs.getInt("activity_id"),
                        rs.getString("activity_name"),
                        rs.getString("event_name"),
                        rs.getTimestamp("registered_on")
                    );
                    registrations.add(dto);
                }
            }
        }
        
        return registrations;
    }

    /**
     * Registers a student for an activity (inserts into activity_participants).
     * Uses transaction to ensure atomicity and updates participant_count.
     * 
     * @param studentId the student ID
     * @param activityId the activity ID
     * @return true if registration successful, false otherwise
     * @throws SQLException if database error occurs (including constraint violations)
     */
    public boolean registerStudentForActivity(int studentId, int activityId) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        if (conn == null) {
            throw new SQLException("Database connection is null");
        }
        
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // Insert registration
            String insertSql = "INSERT INTO activity_participants (activity_id, student_id, registered_on) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, activityId);
                insertStmt.setInt(2, studentId);
                insertStmt.executeUpdate();
            }

            // Update participant count for activity
            updateActivityParticipantCount(conn, activityId);

            // Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cancels a student's registration for an activity (deletes from activity_participants).
     * Uses transaction and updates participant_count.
     * 
     * @param participantId the participant ID (registration ID)
     * @return true if cancellation successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean cancelRegistration(int participantId) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        if (conn == null) {
            throw new SQLException("Database connection is null");
        }
        
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // Get activity_id before deletion
            int activityId = -1;
            String selectSql = "SELECT activity_id FROM activity_participants WHERE participant_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, participantId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        activityId = rs.getInt("activity_id");
                    }
                }
            }

            if (activityId == -1) {
                throw new SQLException("Registration not found");
            }

            // Delete registration
            String deleteSql = "DELETE FROM activity_participants WHERE participant_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, participantId);
                deleteStmt.executeUpdate();
            }

            // Update participant count
            updateActivityParticipantCount(conn, activityId);

            // Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds all activities for a given event.
     * 
     * @param eventId the event ID
     * @return List of ActivityDTO objects
     * @throws SQLException if database error occurs
     */
    public List<ActivityDTO> findActivitiesByEvent(int eventId) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String sql = "SELECT activity_id, activity_name, description, start_time, end_time, participant_count FROM activity WHERE event_id = ? ORDER BY start_time";

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
                    activities.add(dto);
                }
            }
        }
        
        return activities;
    }

    /**
     * Finds all activities (across all events).
     * 
     * @return List of ActivityDTO objects with event_id and event_name
     * @throws SQLException if database error occurs
     */
    public List<ActivityDTO> findAllActivities() throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String sql = "SELECT a.activity_id, a.activity_name, a.description, a.start_time, a.end_time, a.participant_count, e.event_name, e.event_id " +
                     "FROM activity a " +
                     "JOIN event e ON a.event_id = e.event_id " +
                     "ORDER BY e.event_date, a.start_time";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ActivityDTO dto = new ActivityDTO(
                    rs.getInt("a.activity_id"),
                    rs.getString("a.activity_name"),
                    rs.getString("a.description"),
                    rs.getTime("a.start_time"),
                    rs.getTime("a.end_time"),
                    rs.getInt("a.participant_count")
                );
                dto.setEventId(rs.getInt("e.event_id"));
                dto.setEventName(rs.getString("e.event_name"));
                activities.add(dto);
            }
        }
        
        return activities;
    }

    /**
     * Finds all activities in the same event, excluding a specific activity.
     * Used for editing/changing registrations.
     * 
     * @param eventId the event ID
     * @param excludeActivityId the activity ID to exclude
     * @return List of ActivityDTO objects
     * @throws SQLException if database error occurs
     */
    public List<ActivityDTO> findOtherActivitiesInEvent(int eventId, int excludeActivityId) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String sql = "SELECT activity_id, activity_name, description, start_time, end_time, participant_count FROM activity WHERE event_id = ? AND activity_id <> ? ORDER BY start_time";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, excludeActivityId);
            
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
     * Changes a student's registration from one activity to another (within the same event).
     * Uses transaction: deletes old registration, inserts new one, updates counts.
     * 
     * @param participantId the participant ID (registration to delete)
     * @param newActivityId the new activity ID to register for
     * @return true if change successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean changeRegistration(int participantId, int newActivityId) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        if (conn == null) {
            throw new SQLException("Database connection is null");
        }
        
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // Get student_id and old activity_id
            int studentId = -1;
            int oldActivityId = -1;
            String selectSql = "SELECT student_id, activity_id FROM activity_participants WHERE participant_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, participantId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getInt("student_id");
                        oldActivityId = rs.getInt("activity_id");
                    }
                }
            }

            if (studentId == -1 || oldActivityId == -1) {
                throw new SQLException("Registration not found");
            }

            // Delete old registration
            String deleteSql = "DELETE FROM activity_participants WHERE participant_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, participantId);
                deleteStmt.executeUpdate();
            }

            // Insert new registration
            String insertSql = "INSERT INTO activity_participants (activity_id, student_id, registered_on) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, newActivityId);
                insertStmt.setInt(2, studentId);
                insertStmt.executeUpdate();
            }

            // Update participant counts for both activities
            updateActivityParticipantCount(conn, oldActivityId);
            updateActivityParticipantCount(conn, newActivityId);

            // Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a participant registration (simple version without transaction control).
     * Inserts a single row into activity_participants.
     * 
     * @param activityId the activity ID
     * @param studentId the student ID
     * @return true if insertion successful, false otherwise
     * @throws SQLException if database error occurs (including constraint violations)
     */
    public boolean addParticipant(int activityId, int studentId) throws SQLException {
        String sql = "INSERT INTO activity_participants (activity_id, student_id, registered_on) VALUES (?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            stmt.setInt(2, studentId);
            int result = stmt.executeUpdate();
            return result > 0;
        }
    }

    /**
     * Finds all activities that a student is NOT yet registered for.
     * Uses LEFT JOIN to exclude activities where student already has a registration.
     * 
     * @param studentId the student ID
     * @return List of ActivityDTO objects for available activities
     * @throws SQLException if database error occurs
     */
    public List<ActivityDTO> findAvailableActivitiesForStudent(int studentId) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String sql = "SELECT DISTINCT a.activity_id, a.activity_name, a.description, a.start_time, a.end_time, a.participant_count " +
                     "FROM activity a " +
                     "LEFT JOIN activity_participants ap ON a.activity_id = ap.activity_id AND ap.student_id = ? " +
                     "WHERE ap.participant_id IS NULL " +
                     "ORDER BY a.activity_id";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ActivityDTO dto = new ActivityDTO(
                        rs.getInt("a.activity_id"),
                        rs.getString("a.activity_name"),
                        rs.getString("a.description"),
                        rs.getTime("a.start_time"),
                        rs.getTime("a.end_time"),
                        rs.getInt("a.participant_count")
                    );
                    activities.add(dto);
                }
            }
        }
        
        return activities;
    }

    /**
     * Updates the participant_count for an activity based on current registrations.
     * This method should be called within an existing transaction.
     * 
     * @param conn the database connection (must have auto-commit = false)
     * @param activityId the activity ID
     * @throws SQLException if database error occurs
     */
    private void updateActivityParticipantCount(Connection conn, int activityId) throws SQLException {
        // Get current count
        String countSql = "SELECT COUNT(*) FROM activity_participants WHERE activity_id = ?";
        int count = 0;
        try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
            countStmt.setInt(1, activityId);
            try (ResultSet rs = countStmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }

        // Update activity table
        String updateSql = "UPDATE activity SET participant_count = ? WHERE activity_id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, count);
            updateStmt.setInt(2, activityId);
            updateStmt.executeUpdate();
        }

        // Optional: call stored procedure if it exists
        // Note: only uncomment if the procedure exists in your database
        /*
        try {
            String callSql = "CALL update_event_unique_participant_count(?)";
            try (CallableStatement callStmt = conn.prepareCall(callSql)) {
                // Get event_id for this activity
                String eventIdSql = "SELECT event_id FROM activity WHERE activity_id = ?";
                int eventId = -1;
                try (PreparedStatement eventStmt = conn.prepareStatement(eventIdSql)) {
                    eventStmt.setInt(1, activityId);
                    try (ResultSet rs = eventStmt.executeQuery()) {
                        if (rs.next()) {
                            eventId = rs.getInt("event_id");
                        }
                    }
                }
                
                if (eventId != -1) {
                    callStmt.setInt(1, eventId);
                    callStmt.execute();
                }
            }
        } catch (SQLException e) {
            // Procedure may not exist; log and continue
            System.err.println("Note: Stored procedure not available: " + e.getMessage());
        }
        */
    }
}
