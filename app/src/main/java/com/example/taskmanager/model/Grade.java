package com.example.taskmanager.model;

public class Grade {
    private long id;
    private String subjectId;
    private String title;
    private double score;
    private double maxScore;
    private String gradeType;
    private String date;

    public Grade(long id, String subjectId, String title, double score, double maxScore,
                 String gradeType, String date) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.score = score;
        this.maxScore = maxScore;
        this.gradeType = gradeType;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    public String getGradeType() { return gradeType; }
    public void setGradeType(String gradeType) { this.gradeType = gradeType; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}

