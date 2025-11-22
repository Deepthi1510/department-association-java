package com.deptassoc.model;

import java.sql.Time;

/**
 * POJO representing an Activity entity.
 */
public class Activity {
    private int activityId;
    private int eventId;
    private String activityName;
    private String description;
    private Time startTime;
    private Time endTime;
    private int participantCount;
    
    public Activity() {}
    
    public Activity(int activityId, int eventId, String activityName, String description,
                    Time startTime, Time endTime, int participantCount) {
        this.activityId = activityId;
        this.eventId = eventId;
        this.activityName = activityName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participantCount = participantCount;
    }
    
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    
    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", eventId=" + eventId +
                ", activityName='" + activityName + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", participantCount=" + participantCount +
                '}';
    }
}
