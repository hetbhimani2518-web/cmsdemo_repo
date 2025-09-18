package com.example.cms;

public class student_events_model_admin {

    private String course;
    private String title;
    private String description;
    private String date;

    public student_events_model_admin() {
    }

    public student_events_model_admin(String course, String title, String description, String date) {
        this.course = course;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
