package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event entity.
 */
public class EventDao {
    
    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_EVENTS)) {
            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        }
        return events;
    }
    
    public Event findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_EVENT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEvent(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all events for a specific association.
     */
    public List<Event> findByAssociation(int assocId) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_EVENTS_BY_ASSOCIATION)) {
            stmt.setInt(1, assocId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRowToEvent(rs));
                }
            }
        }
        return events;
    }
    
    public void insert(Event event) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_EVENT)) {
            stmt.setInt(1, event.getAssocId());
            stmt.setString(2, event.getEventName());
            stmt.setDate(3, event.getEventDate());
            stmt.setString(4, event.getVenue());
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getParticipantCount());
            stmt.executeUpdate();
        }
    }
    
    public void update(Event event) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_EVENT)) {
            stmt.setInt(1, event.getAssocId());
            stmt.setString(2, event.getEventName());
            stmt.setDate(3, event.getEventDate());
            stmt.setString(4, event.getVenue());
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getParticipantCount());
            stmt.setInt(7, event.getEventId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_EVENT)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        return new Event(
            rs.getInt("event_id"),
            rs.getInt("assoc_id"),
            rs.getString("event_name"),
            rs.getDate("event_date"),
            rs.getString("venue"),
            rs.getString("description"),
            rs.getInt("participant_count")
        );
    }
}
