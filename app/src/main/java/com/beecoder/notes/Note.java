package com.beecoder.notes;

import com.google.firebase.Timestamp;

public class Note {
    private String text;
    private boolean isCompleted;
    private Timestamp created;
    private String userId;

    public Note() {

    }

    public Note(String text, boolean isCompleted, Timestamp created, String userId) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.created = created;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
