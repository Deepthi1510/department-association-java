package com.deptassoc.dao;

import com.deptassoc.db.DBConnectionManager;
import com.deptassoc.db.SQLConstants;
import com.deptassoc.model.AssociationMember;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}
