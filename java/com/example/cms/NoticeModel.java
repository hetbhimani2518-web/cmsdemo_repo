package com.example.cms;

public class NoticeModel {

    private String title, content, date, facultyName , facultyID , year, semester, program , division , noticeID , autoDeleteDate;
    long timestamp;

    public NoticeModel() {
    }

    public NoticeModel(String title, String content, String date, String facultyName, String facultyID,
                       String year, String semester, String division, String program,
                       long timestamp, String noticeID, String autoDeleteDate) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.facultyName = facultyName;
        this.facultyID = facultyID;
        this.year = year;
        this.semester = semester;
        this.division = division;
        this.program = program;
        this.timestamp = timestamp;
        this.noticeID = noticeID;
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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(String facultyID) {
        this.facultyID = facultyID;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    public String getAutoDeleteDate() {
        return autoDeleteDate;
    }

    public void setAutoDeleteDate(String autoDeleteDate) {
        this.autoDeleteDate = autoDeleteDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

