package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.ActivityParticipant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ActivityParticipant entity.
 */
public class ActivityParticipantDao {
    
    public List<ActivityParticipant> findAll() throws SQLException {
        List<ActivityParticipant> participants = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_ACTIVITY_PARTICIPANTS)) {
            while (rs.next()) {
                participants.add(mapRowToParticipant(rs));
            }
        }
        return participants;
    }
    
    public ActivityParticipant findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ACTIVITY_PARTICIPANT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToParticipant(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all participants in an activity.
     */
    public List<ActivityParticipant> findByActivity(int activityId) throws SQLException {
        List<ActivityParticipant> participants = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_PARTICIPANTS_BY_ACTIVITY)) {
            stmt.setInt(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(mapRowToParticipant(rs));
                }
            }
        }
        return participants;
    }
    
    public void insert(ActivityParticipant participant) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_ACTIVITY_PARTICIPANT)) {
            stmt.setInt(1, participant.getActivityId());
            stmt.setInt(2, participant.getStudentId());
            stmt.setTimestamp(3, participant.getRegisteredOn());
            stmt.executeUpdate();
        }
    }
    
    public void update(ActivityParticipant participant) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_ACTIVITY_PARTICIPANT)) {
            stmt.setInt(1, participant.getActivityId());
            stmt.setInt(2, participant.getStudentId());
            stmt.setTimestamp(3, participant.getRegisteredOn());
            stmt.setInt(4, participant.getParticipantId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_ACTIVITY_PARTICIPANT)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private ActivityParticipant mapRowToParticipant(ResultSet rs) throws SQLException {
        return new ActivityParticipant(
            rs.getInt("participant_id"),
            rs.getInt("activity_id"),
            rs.getInt("student_id"),
            rs.getTimestamp("registered_on")
        );
    }
}
