package com.example.hoctap.models;

public class Subject {
    private long id;
    private String subjectCode;
    private String subjectName;
    private String semester;
    private String description;
    private String createdAt;

    public Subject() {
    }

    public Subject(long id, String subjectCode, String subjectName, String semester, String description, String createdAt) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.semester = semester;
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

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
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

    @Override
    public String toString() {
        return subjectName + " (" + subjectCode + ")";
    }
}

