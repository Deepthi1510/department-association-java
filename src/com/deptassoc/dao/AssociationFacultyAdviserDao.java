package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.AssociationFacultyAdviser;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AssociationFacultyAdviser entity.
 */
public class AssociationFacultyAdviserDao {
    
    public List<AssociationFacultyAdviser> findAll() throws SQLException {
        List<AssociationFacultyAdviser> advisers = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_ASSOCIATION_FACULTY_ADVISERS)) {
            while (rs.next()) {
                advisers.add(mapRowToAdviser(rs));
            }
        }
        return advisers;
    }
    
    public AssociationFacultyAdviser findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ASSOCIATION_FACULTY_ADVISER_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAdviser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all advisers for an association.
     */
    public List<AssociationFacultyAdviser> findByAssociation(int assocId) throws SQLException {
        List<AssociationFacultyAdviser> advisers = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ADVISERS_BY_ASSOCIATION)) {
            stmt.setInt(1, assocId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    advisers.add(mapRowToAdviser(rs));
                }
            }
        }
        return advisers;
    }
    
    public void insert(AssociationFacultyAdviser adviser) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_ASSOCIATION_FACULTY_ADVISER)) {
            stmt.setInt(1, adviser.getAssocId());
            stmt.setInt(2, adviser.getFacultyId());
            stmt.setString(3, adviser.getRole());
            stmt.executeUpdate();
        }
    }
    
    public void update(AssociationFacultyAdviser adviser) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_ASSOCIATION_FACULTY_ADVISER)) {
            stmt.setInt(1, adviser.getAssocId());
            stmt.setInt(2, adviser.getFacultyId());
            stmt.setString(3, adviser.getRole());
            stmt.setInt(4, adviser.getAdviserId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_ASSOCIATION_FACULTY_ADVISER)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private AssociationFacultyAdviser mapRowToAdviser(ResultSet rs) throws SQLException {
        return new AssociationFacultyAdviser(
            rs.getInt("adviser_id"),
            rs.getInt("assoc_id"),
            rs.getInt("faculty_id"),
            rs.getString("role")
        );
    }
}
