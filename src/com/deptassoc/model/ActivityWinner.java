package com.deptassoc.model;

/**
 * POJO representing an ActivityWinner entity.
 */
public class ActivityWinner {
    private int winnerId;
    private int activityId;
    private int studentId;
    private int position;
    
    public ActivityWinner() {}
    
    public ActivityWinner(int winnerId, int activityId, int studentId, int position) {
        this.winnerId = winnerId;
        this.activityId = activityId;
        this.studentId = studentId;
        this.position = position;
    }
    
    public int getWinnerId() { return winnerId; }
    public void setWinnerId(int winnerId) { this.winnerId = winnerId; }
    
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    
    @Override
    public String toString() {
        return "ActivityWinner{" +
                "winnerId=" + winnerId +
                ", activityId=" + activityId +
                ", studentId=" + studentId +
                ", position=" + position +
                '}';
    }
}
