package com.deptassoc.model;

/**
 * POJO representing an AssociationFacultyAdviser entity.
 */
public class AssociationFacultyAdviser {
    private int adviserId;
    private int assocId;
    private int facultyId;
    private String role;
    
    public AssociationFacultyAdviser() {}
    
    public AssociationFacultyAdviser(int adviserId, int assocId, int facultyId, String role) {
        this.adviserId = adviserId;
        this.assocId = assocId;
        this.facultyId = facultyId;
        this.role = role;
    }
    
    public int getAdviserId() { return adviserId; }
    public void setAdviserId(int adviserId) { this.adviserId = adviserId; }
    
    public int getAssocId() { return assocId; }
    public void setAssocId(int assocId) { this.assocId = assocId; }
    
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    @Override
    public String toString() {
        return "AssociationFacultyAdviser{" +
                "adviserId=" + adviserId +
                ", assocId=" + assocId +
                ", facultyId=" + facultyId +
                ", role='" + role + '\'' +
                '}';
    }
}
