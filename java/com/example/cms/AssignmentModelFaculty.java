package com.example.cms;

import android.widget.TextView;

public class AssignmentModelFaculty {
    private String assignmentId;
    private String title;
    private String description;
    private String deadline;
    private String driveLink;
    private String facultyName;
    private String facultyID;
    private String program;
    private String year;
    private String semester;
    private String division;
    private String currentDate;
    private long timestamp;
    private String autoDelete;

    public AssignmentModelFaculty() {
    }

    public AssignmentModelFaculty(String assignmentId, String title, String description, String deadline, String driveLink, String facultyName, String facultyID, String currentDate, String program, String year, String semester, String division, long timestamp , String autoDelete) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.driveLink = driveLink;
        this.facultyName = facultyName;
        this.facultyID = facultyID;
        this.currentDate = currentDate;
        this.program = program;
        this.year = year;
        this.semester = semester;
        this.division = division;
        this.timestamp = timestamp;
        this.autoDelete = autoDelete;
    }

    public String getAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(String autoDelete) {
        this.autoDelete = autoDelete;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDriveLink() {
        return driveLink;
    }

    public void setDriveLink(String driveLink) {
        this.driveLink = driveLink;
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

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
