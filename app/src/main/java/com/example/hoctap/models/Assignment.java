package com.example.hoctap.models;

public class Assignment {
    private long id;
    private long subjectId;
    private String title;
    private String description;
    private String deadline;
    private String reminderTime;
    private int priority;
    private int status;
    private String createdAt;

    // For UI display
    private String subjectName;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_COMPLETED = 2;

    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;

    public Assignment() {
    }

    public Assignment(long id, long subjectId, String title, String description, String deadline,
                      String reminderTime, int priority, int status, String createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.reminderTime = reminderTime;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getPriorityText() {
        switch (priority) {
            case PRIORITY_LOW:
                return "Low";
            case PRIORITY_MEDIUM:
                return "Medium";
            case PRIORITY_HIGH:
                return "High";
            default:
                return "Unknown";
        }
    }

    public String getStatusText() {
        switch (status) {
            case STATUS_PENDING:
                return "Pending";
            case STATUS_IN_PROGRESS:
                return "In Progress";
            case STATUS_COMPLETED:
                return "Completed";
            default:
                return "Unknown";
        }
    }
}

