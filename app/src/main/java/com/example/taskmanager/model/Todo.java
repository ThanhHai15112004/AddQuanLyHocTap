package com.example.taskmanager.model;

public class Todo {
    private long id;
    private long userId;
    private String title;
    private boolean isDone;
    private String dueDate;
    private long noteId;

    public Todo(long id, long userId, String title, boolean isDone, String dueDate, long noteId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.isDone = isDone;
        this.dueDate = dueDate;
        this.noteId = noteId;
    }

    public Todo(long userId, String title, boolean isDone, String dueDate, long noteId) {
        this.userId = userId;
        this.title = title;
        this.isDone = isDone;
        this.dueDate = dueDate;
        this.noteId = noteId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public long getNoteId() { return noteId; }
    public void setNoteId(long noteId) { this.noteId = noteId; }
}

