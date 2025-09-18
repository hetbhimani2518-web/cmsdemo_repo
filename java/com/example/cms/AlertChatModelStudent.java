package com.example.cms;

public class AlertChatModelStudent {
    private String facultyId;
    private String facultyName;
    private String timestamp;
    private int unreadCount;

    public AlertChatModelStudent() {
    }

    public AlertChatModelStudent(String facultyId, String facultyName, String timestamp, int unreadCount) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
