package com.deptassoc.model;

/**
 * POJO representing a Faculty entity.
 */
public class Faculty {
    private int facultyId;
    private String fName;
    private String fEmail;
    private String fPhone;
    private String designation;
    
    public Faculty() {}
    
    public Faculty(int facultyId, String fName, String fEmail, String fPhone, String designation) {
        this.facultyId = facultyId;
        this.fName = fName;
        this.fEmail = fEmail;
        this.fPhone = fPhone;
        this.designation = designation;
    }
    
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    
    public String getFName() { return fName; }
    public void setFName(String fName) { this.fName = fName; }
    
    public String getFEmail() { return fEmail; }
    public void setFEmail(String fEmail) { this.fEmail = fEmail; }
    
    public String getFPhone() { return fPhone; }
    public void setFPhone(String fPhone) { this.fPhone = fPhone; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    @Override
    public String toString() {
        return "Faculty{" +
                "facultyId=" + facultyId +
                ", fName='" + fName + '\'' +
                ", fEmail='" + fEmail + '\'' +
                ", fPhone='" + fPhone + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }
}
