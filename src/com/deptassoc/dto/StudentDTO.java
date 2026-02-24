package com.deptassoc.dto;

import java.sql.Timestamp;

/**
 * Data Transfer Object for students.
 */
public class StudentDTO {
    private int studentId;
    private String name;
    private String email;
    private String phone;
    private Timestamp registeredOn;

    public StudentDTO(int studentId, String name, String email, String phone) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(Timestamp registeredOn) {
        this.registeredOn = registeredOn;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", registeredOn=" + registeredOn +
                '}';
    }
}
