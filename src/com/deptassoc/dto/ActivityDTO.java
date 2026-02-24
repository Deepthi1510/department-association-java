package com.deptassoc.dto;

import java.sql.Time;

/**
 * Data Transfer Object for activities.
 */
public class ActivityDTO {
    private int activityId;
    private String activityName;
    private String description;
    private Time startTime;
    private Time endTime;
    private int participantCount;
    private int eventId;
    private String eventName;

    public ActivityDTO(int activityId, String activityName, String description, Time startTime, Time endTime, int participantCount) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantCount = participantCount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", participantCount=" + participantCount +
                ", eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                '}';
    }
}
