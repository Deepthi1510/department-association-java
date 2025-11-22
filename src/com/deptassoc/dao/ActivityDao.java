package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Activity entity.
 * Provides CRUD operations and common queries.
 */
public class ActivityDao {
    
    /**
     * Retrieves all activities.
     */
    public List<Activity> findAll() throws SQLException {
        List<Activity> activities = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_ACTIVITIES)) {
            while (rs.next()) {
                activities.add(mapRowToActivity(rs));
            }
        }
        return activities;
    }
    
    /**
     * Finds an activity by ID.
     */
    public Activity findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ACTIVITY_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToActivity(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all activities for a specific event.
     */
    public List<Activity> findByEvent(int eventId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ACTIVITIES_BY_EVENT)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapRowToActivity(rs));
                }
            }
        }
        return activities;
    }
    
    /**
     * Inserts a new activity.
     */
    public void insert(Activity activity) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_ACTIVITY)) {
            stmt.setInt(1, activity.getEventId());
            stmt.setString(2, activity.getActivityName());
            stmt.setString(3, activity.getDescription());
            stmt.setTime(4, activity.getStartTime());
            stmt.setTime(5, activity.getEndTime());
            stmt.setInt(6, activity.getParticipantCount());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Updates an existing activity.
     */
    public void update(Activity activity) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_ACTIVITY)) {
            stmt.setInt(1, activity.getEventId());
            stmt.setString(2, activity.getActivityName());
            stmt.setString(3, activity.getDescription());
            stmt.setTime(4, activity.getStartTime());
            stmt.setTime(5, activity.getEndTime());
            stmt.setInt(6, activity.getParticipantCount());
            stmt.setInt(7, activity.getActivityId());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Deletes an activity by ID.
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_ACTIVITY)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to an Activity object.
     */
    private Activity mapRowToActivity(ResultSet rs) throws SQLException {
        return new Activity(
            rs.getInt("activity_id"),
            rs.getInt("event_id"),
            rs.getString("activity_name"),
            rs.getString("description"),
            rs.getTime("start_time"),
            rs.getTime("end_time"),
            rs.getInt("participant_count")
        );
    }
}
