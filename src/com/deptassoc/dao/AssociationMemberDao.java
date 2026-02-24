package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.dto.ActivityDTO;
import com.deptassoc.model.AssociationMember;
import com.deptassoc.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for AssociationMember entity.
 */
public class AssociationMemberDao {
    
    public List<AssociationMember> findAll() throws SQLException {
        List<AssociationMember> members = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLConstants.FIND_ALL_ASSOCIATION_MEMBERS)) {
            while (rs.next()) {
                members.add(mapRowToAssociationMember(rs));
            }
        }
        return members;
    }
    
    public AssociationMember findById(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_ASSOCIATION_MEMBER_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAssociationMember(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all members of an association.
     */
    public List<AssociationMember> findByAssociation(int assocId) throws SQLException {
        List<AssociationMember> members = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.FIND_MEMBERS_BY_ASSOCIATION)) {
            stmt.setInt(1, assocId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRowToAssociationMember(rs));
                }
            }
        }
        return members;
    }
    
    public void insert(AssociationMember member) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.INSERT_ASSOCIATION_MEMBER)) {
            stmt.setInt(1, member.getAssocId());
            stmt.setInt(2, member.getStudentId());
            stmt.setString(3, member.getRole());
            stmt.setDate(4, member.getJoinedDate());
            stmt.executeUpdate();
        }
    }
    
    public void update(AssociationMember member) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.UPDATE_ASSOCIATION_MEMBER)) {
            stmt.setInt(1, member.getAssocId());
            stmt.setInt(2, member.getStudentId());
            stmt.setString(3, member.getRole());
            stmt.setDate(4, member.getJoinedDate());
            stmt.setInt(5, member.getMemberId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLConstants.DELETE_ASSOCIATION_MEMBER)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private AssociationMember mapRowToAssociationMember(ResultSet rs) throws SQLException {
        return new AssociationMember(
            rs.getInt("member_id"),
            rs.getInt("assoc_id"),
            rs.getInt("student_id"),
            rs.getString("role"),
            rs.getDate("joined_date")
        );
    }

    /**
     * Get all association members with their roles.
     * Returns: Map of role -> List of student names
     */
    public Map<String, List<String>> getAssociationMembersAndRoles() throws SQLException {
        Map<String, List<String>> roleMembers = new HashMap<>();
        String sql = "SELECT am.role, s.s_name " +
                     "FROM association_members am " +
                     "JOIN student s ON am.student_id = s.student_id " +
                     "ORDER BY am.role, s.s_name";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String role = rs.getString("role");
                String sName = rs.getString("s_name");
                
                roleMembers.computeIfAbsent(role, k -> new ArrayList<>()).add(sName);
            }
        }
        
        return roleMembers;
    }

    /**
     * Get total count of association members.
     */
    public int getTotalMemberCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM association_members";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
}
