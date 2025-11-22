package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.Association;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Association entity.
 * Provides CRUD operations and common queries.
 */
public class AssociationDao {
    
    /**
     * Retrieves all associations.
     */
    public List<Association> findAll() throws SQLException {
        List<Association> associations = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_ASSOCIATIONS)) {
            while (rs.next()) {
                associations.add(mapRowToAssociation(rs));
            }
        }
        return associations;
    }
    
    /**
     * Finds an association by ID.
     */
    public Association findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ASSOCIATION_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAssociation(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Inserts a new association.
     */
    public void insert(Association association) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_ASSOCIATION)) {
            stmt.setString(1, association.getAssocName());
            stmt.setInt(2, association.getEstablishmentYear());
            stmt.setInt(3, association.getDepartmentId());
            stmt.setString(4, association.getDescription());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Updates an existing association.
     */
    public void update(Association association) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_ASSOCIATION)) {
            stmt.setString(1, association.getAssocName());
            stmt.setInt(2, association.getEstablishmentYear());
            stmt.setInt(3, association.getDepartmentId());
            stmt.setString(4, association.getDescription());
            stmt.setInt(5, association.getAssocId());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Deletes an association by ID.
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_ASSOCIATION)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Maps a ResultSet row to an Association object.
     */
    private Association mapRowToAssociation(ResultSet rs) throws SQLException {
        return new Association(
            rs.getInt("assoc_id"),
            rs.getString("assoc_name"),
            rs.getInt("establishment_year"),
            rs.getInt("department_id"),
            rs.getString("description")
        );
    }
}
