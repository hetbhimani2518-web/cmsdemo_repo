package com.example.cms;

public class faculty_events_model_admin {
    private String title, content, date;

    public faculty_events_model_admin(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public faculty_events_model_admin() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
