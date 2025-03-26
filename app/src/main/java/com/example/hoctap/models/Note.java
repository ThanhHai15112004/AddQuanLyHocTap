package com.example.hoctap.models;

public class Note {
    private long id;
    private long subjectId;
    private String title;
    private String description;
    private String createdAt;

    // For UI display
    private String subjectName;

    public Note() {
    }

    public Note(long id, long subjectId, String title, String description, String createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.description = description;
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
}

