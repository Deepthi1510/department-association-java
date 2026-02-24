package com.deptassoc.swingui;

import com.deptassoc.db.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Authentication service using direct database lookups.
 * Supports 3 login types: Student, Faculty, Association Member
 * Uses plain-text matching based on database values (no hashing).
 */
public class AuthService {

    /**
     * Authenticates a user based on login type and credentials.
     * 
     * @param loginType "STUDENT", "FACULTY", or "ASSOCIATION_MEMBER"
     * @param username  Name to match (s_name or f_name)
     * @param password  Email to match (s_email or f_email)
     * @return AuthResult with success flag and user details
     */
    public static AuthResult authenticate(String loginType, String username, String password) {
        try {
            switch (loginType) {
                case "STUDENT":
                    return authenticateStudent(username, password);
                case "FACULTY":
                    return authenticateFaculty(username, password);
                case "ASSOCIATION_MEMBER":
                    return authenticateAssociationMember(username, password);
                default:
                    return new AuthResult(false, null, 0, username, null);
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return new AuthResult(false, null, 0, username, null);
        }
    }

    /**
     * Authenticates student: username = s_name AND password = s_email
     */
    private static AuthResult authenticateStudent(String username, String password) throws Exception {
        String sql = "SELECT student_id, s_name, s_email FROM student WHERE s_name = ? AND s_email = ?";
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String sName = rs.getString("s_name");
                    return new AuthResult(true, "STUDENT", studentId, sName, sName);
                }
            }
        }
        
        return new AuthResult(false, null, 0, username, null);
    }

    /**
     * Authenticates faculty: username = f_name AND password = f_email
     */
    private static AuthResult authenticateFaculty(String username, String password) throws Exception {
        String sql = "SELECT faculty_id, f_name, f_email FROM faculty WHERE f_name = ? AND f_email = ?";
        
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int facultyId = rs.getInt("faculty_id");
                    String fName = rs.getString("f_name");
                    return new AuthResult(true, "FACULTY", facultyId, fName, fName);
                }
            }
        }
        
        return new AuthResult(false, null, 0, username, null);
    }

    /**
     * Authenticates association member:
     * username = student_id (integer) AND password = role (string)
     * Query: SELECT am.student_id, am.role FROM association_members am
     *        JOIN student s ON am.student_id = s.student_id
     *        WHERE am.student_id = ? AND am.role = ?;
     */
    private static AuthResult authenticateAssociationMember(String username, String password) throws Exception {
        try {
            // Parse username as student_id
            int studentId = Integer.parseInt(username);
            
            String sql = "SELECT am.student_id, am.role, s.s_name " +
                         "FROM association_members am " +
                         "JOIN student s ON am.student_id = s.student_id " +
                         "WHERE am.student_id = ? AND am.role = ?";
            
            try (Connection conn = DBConnectionManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, studentId);
                stmt.setString(2, password);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int memberId = rs.getInt("student_id");
                        String memberRole = rs.getString("role");
                        String sName = rs.getString("s_name");
                        return new AuthResult(true, "ASSOCIATION_MEMBER", memberId, username, sName, memberRole);
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Username is not a valid integer
            return new AuthResult(false, null, 0, username, null);
        }
        
        return new AuthResult(false, null, 0, username, null);
    }
}
