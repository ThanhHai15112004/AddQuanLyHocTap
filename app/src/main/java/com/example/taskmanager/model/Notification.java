package com.example.taskmanager.model;

public class Notification {
    private long id;
    private long userId;
    private long assignmentId;
    private  String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private long scheduleId;
    private String remindAt;
    private boolean isSent;

    public Notification(long id, long userId, long assignmentId, long scheduleId,
                        String remindAt, boolean isSent, String message) {
        this.id = id;
        this.userId = userId;
        this.assignmentId = assignmentId;
        this.scheduleId = scheduleId;
        this.remindAt = remindAt;
        this.isSent = isSent;
        this.message = message;
    }

    public Notification(long userId, long assignmentId, long scheduleId,
                        String remindAt, boolean isSent, String message) {
        this.userId = userId;
        this.assignmentId = assignmentId;
        this.scheduleId = scheduleId;
        this.remindAt = remindAt;
        this.isSent = isSent;
        this.message = message;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(long assignmentId) { this.assignmentId = assignmentId; }
    public long getScheduleId() { return scheduleId; }
    public void setScheduleId(long scheduleId) { this.scheduleId = scheduleId; }
    public String getRemindAt() { return remindAt; }
    public void setRemindAt(String remindAt) { this.remindAt = remindAt; }
    public boolean isSent() { return isSent; }
    public void setSent(boolean sent) { isSent = sent; }
}

