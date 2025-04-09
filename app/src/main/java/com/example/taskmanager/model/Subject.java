package com.example.taskmanager.model;

public class Subject {
    private long id;
    private long userId;
    private String name;
    private String code;
    private String colorLabel;

    public Subject(long id, long userId, String name, String code, String colorLabel) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.code = code;
        this.colorLabel = colorLabel;
    }

    public Subject(long userId, String name, String code, String colorLabel) {
        this.userId = userId;
        this.name = name;
        this.code = code;
        this.colorLabel = colorLabel;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getColorLabel() { return colorLabel; }
    public void setColorLabel(String colorLabel) { this.colorLabel = colorLabel; }
}

