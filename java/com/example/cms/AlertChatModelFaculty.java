package com.example.cms;

public class AlertChatModelFaculty {
    private String studentName, studentId, timestamp;
    private int unreadCount;

    public AlertChatModelFaculty() {
    }

    public AlertChatModelFaculty(String studentName, String studentId, String timestamp, int unreadCount) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
