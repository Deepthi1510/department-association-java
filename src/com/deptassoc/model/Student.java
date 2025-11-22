package com.deptassoc.model;

/**
 * POJO representing a Student entity.
 */
public class Student {
    private int studentId;
    private String sName;
    private String sEmail;
    private String phone;
    
    public Student() {}
    
    public Student(int studentId, String sName, String sEmail, String phone) {
        this.studentId = studentId;
        this.sName = sName;
        this.sEmail = sEmail;
        this.phone = phone;
    }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public String getSName() { return sName; }
    public void setSName(String sName) { this.sName = sName; }
    
    public String getSEmail() { return sEmail; }
    public void setSEmail(String sEmail) { this.sEmail = sEmail; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", sName='" + sName + '\'' +
                ", sEmail='" + sEmail + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
