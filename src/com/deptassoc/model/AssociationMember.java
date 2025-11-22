package com.deptassoc.model;

import java.sql.Date;

/**
 * POJO representing an AssociationMember entity.
 */
public class AssociationMember {
    private int memberId;
    private int assocId;
    private int studentId;
    private String role;
    private Date joinedDate;
    
    public AssociationMember() {}
    
    public AssociationMember(int memberId, int assocId, int studentId, String role, Date joinedDate) {
        this.memberId = memberId;
        this.assocId = assocId;
        this.studentId = studentId;
        this.role = role;
        this.joinedDate = joinedDate;
    }
    
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    
    public int getAssocId() { return assocId; }
    public void setAssocId(int assocId) { this.assocId = assocId; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Date getJoinedDate() { return joinedDate; }
    public void setJoinedDate(Date joinedDate) { this.joinedDate = joinedDate; }
    
    @Override
    public String toString() {
        return "AssociationMember{" +
                "memberId=" + memberId +
                ", assocId=" + assocId +
                ", studentId=" + studentId +
                ", role='" + role + '\'' +
                ", joinedDate=" + joinedDate +
                '}';
    }
}
