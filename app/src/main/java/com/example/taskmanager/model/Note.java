package com.example.taskmanager.model;

public class Note {
    private long id;
    private long userId;
    private String subjectId;
    private String content;
    private String createdAt;

    public Note(long id, long userId, String subjectId, String content, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.subjectId = subjectId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Note(long userId, String subjectId, String content, String createdAt) {
        this.userId = userId;
        this.subjectId = subjectId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

