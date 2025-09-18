package com.example.cms;

public class student_notice_model_admin {
    private String title, content, date, course, semester , autoDeleteDate;

    public student_notice_model_admin() {
    }

    public student_notice_model_admin(String title, String content, String date, String course, String semester, String autoDeleteDate) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.course = course;
        this.semester = semester;
        this.autoDeleteDate = autoDeleteDate;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAutoDeleteDate() {
        return autoDeleteDate;
    }

    public void setAutoDeleteDate(String autoDeleteDate) {
        this.autoDeleteDate = autoDeleteDate;
    }
}
