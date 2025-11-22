package com.deptassoc.model;

import java.sql.Date;

/**
 * POJO representing an Event entity.
 */
public class Event {
    private int eventId;
    private int assocId;
    private String eventName;
    private Date eventDate;
    private String venue;
    private String description;
    private int participantCount;
    
    public Event() {}
    
    public Event(int eventId, int assocId, String eventName, Date eventDate,
                 String venue, String description, int participantCount) {
        this.eventId = eventId;
        this.assocId = assocId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.venue = venue;
        this.description = description;
        this.participantCount = participantCount;
    }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public int getAssocId() { return assocId; }
    public void setAssocId(int assocId) { this.assocId = assocId; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", assocId=" + assocId +
                ", eventName='" + eventName + '\'' +
                ", eventDate=" + eventDate +
                ", venue='" + venue + '\'' +
                ", description='" + description + '\'' +
                ", participantCount=" + participantCount +
                '}';
    }
}
