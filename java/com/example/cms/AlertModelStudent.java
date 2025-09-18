package com.example.cms;

public class AlertModelStudent {
    private String assignmentID, title, message, program, year, semester;
    private Long timestamp;

    public AlertModelStudent() {
    }

    public AlertModelStudent(String assignmentID, String title, String message, Long timestamp, String program, String year, String semester) {
        this.assignmentID = assignmentID;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.program = program;
        this.year = year;
        this.semester = semester;
    }

    public String getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
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
}
