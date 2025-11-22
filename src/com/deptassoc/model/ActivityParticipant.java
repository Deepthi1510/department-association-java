package com.deptassoc.model;

import java.sql.Timestamp;

/**
 * POJO representing an ActivityParticipant entity.
 */
public class ActivityParticipant {
    private int participantId;
    private int activityId;
    private int studentId;
    private Timestamp registeredOn;
    
    public ActivityParticipant() {}
    
    public ActivityParticipant(int participantId, int activityId, int studentId, Timestamp registeredOn) {
        this.participantId = participantId;
        this.activityId = activityId;
        this.studentId = studentId;
        this.registeredOn = registeredOn;
    }
    
    public int getParticipantId() { return participantId; }
    public void setParticipantId(int participantId) { this.participantId = participantId; }
    
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public Timestamp getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(Timestamp registeredOn) { this.registeredOn = registeredOn; }
    
    @Override
    public String toString() {
        return "ActivityParticipant{" +
                "participantId=" + participantId +
                ", activityId=" + activityId +
                ", studentId=" + studentId +
                ", registeredOn=" + registeredOn +
                '}';
    }
}
