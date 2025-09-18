package com.example.cms;

public class AssignmentModelStudent {
    private String title, description, deadline, facultyName, driveLink , year, semester, program , autoDeleteDate , facultyID , assignmentID , currentDate;
    long timestamp;

    public AssignmentModelStudent() {
    }

    public AssignmentModelStudent(String title, String description, String deadline, String facultyName, String driveLink, String year, String semester, String program, String autoDeleteDate, String facultyID, String assignmentID, String currentDate, long timestamp) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.facultyName = facultyName;
        this.driveLink = driveLink;
        this.year = year;
        this.semester = semester;
        this.program = program;
        this.autoDeleteDate = autoDeleteDate;
        this.facultyID = facultyID;
        this.assignmentID = assignmentID;
        this.currentDate = currentDate;
        this.timestamp = timestamp;
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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getDriveLink() {
        return driveLink;
    }

    public void setDriveLink(String driveLink) {
        this.driveLink = driveLink;
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

    public String getAutoDeleteDate() {
        return autoDeleteDate;
    }

    public void setAutoDeleteDate(String autoDeleteDate) {
        this.autoDeleteDate = autoDeleteDate;
    }

    public String getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(String facultyID) {
        this.facultyID = facultyID;
    }

    public String getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
