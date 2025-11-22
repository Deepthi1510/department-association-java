package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.Faculty;
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
}
