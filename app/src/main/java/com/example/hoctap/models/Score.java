package com.example.hoctap.models;

public class Score {
    private long id;
    private long subjectId;
    private String scoreType;
    private double scoreValue;
    private double scoreWeight;
    private String createdAt;

    // For UI display
    private String subjectName;

    public Score() {
    }

    public Score(long id, long subjectId, String scoreType, double scoreValue, double scoreWeight, String createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.scoreType = scoreType;
        this.scoreValue = scoreValue;
        this.scoreWeight = scoreWeight;
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

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
    }

    public double getScoreWeight() {
        return scoreWeight;
    }

    public void setScoreWeight(double scoreWeight) {
        this.scoreWeight = scoreWeight;
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

