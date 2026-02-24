package com.deptassoc.dto;

import java.sql.Timestamp;

/**
 * Data Transfer Object for student activity registrations.
 */
public class RegistrationDTO {
    private int participantId;
    private int activityId;
    private String activityName;
    private String eventName;
    private Timestamp registeredOn;

    public RegistrationDTO(int participantId, int activityId, String activityName, String eventName, Timestamp registeredOn) {
        this.participantId = participantId;
        this.activityId = activityId;
        this.activityName = activityName;
        this.eventName = eventName;
        this.registeredOn = registeredOn;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Timestamp getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(Timestamp registeredOn) {
        this.registeredOn = registeredOn;
    }

    @Override
    public String toString() {
        return "RegistrationDTO{" +
                "participantId=" + participantId +
                ", activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", eventName='" + eventName + '\'' +
                ", registeredOn=" + registeredOn +
                '}';
    }
}
