package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.ActivityWinner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ActivityWinner entity.
 */
public class ActivityWinnerDao {
    
    public List<ActivityWinner> findAll() throws SQLException {
        List<ActivityWinner> winners = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_ACTIVITY_WINNERS)) {
            while (rs.next()) {
                winners.add(mapRowToWinner(rs));
            }
        }
        return winners;
    }
    
    public ActivityWinner findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ACTIVITY_WINNER_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWinner(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all winners of an activity.
     */
    public List<ActivityWinner> findByActivity(int activityId) throws SQLException {
        List<ActivityWinner> winners = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_WINNERS_BY_ACTIVITY)) {
            stmt.setInt(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    winners.add(mapRowToWinner(rs));
                }
            }
        }
        return winners;
    }
    
    public void insert(ActivityWinner winner) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_ACTIVITY_WINNER)) {
            stmt.setInt(1, winner.getActivityId());
            stmt.setInt(2, winner.getStudentId());
            stmt.setInt(3, winner.getPosition());
            stmt.executeUpdate();
        }
    }
    
    public void update(ActivityWinner winner) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_ACTIVITY_WINNER)) {
            stmt.setInt(1, winner.getActivityId());
            stmt.setInt(2, winner.getStudentId());
            stmt.setInt(3, winner.getPosition());
            stmt.setInt(4, winner.getWinnerId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_ACTIVITY_WINNER)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private ActivityWinner mapRowToWinner(ResultSet rs) throws SQLException {
        return new ActivityWinner(
            rs.getInt("winner_id"),
            rs.getInt("activity_id"),
            rs.getInt("student_id"),
            rs.getInt("position")
        );
    }
}
