package com.example.cms;

public class NoticeAlertModelStudent {
    private String noticeTitle;
    private String noticeDate;
    private String studentProgram;
    private String studentYear;
    private String studentSemester;
    private boolean isRead;

    public NoticeAlertModelStudent() {
    }

    public NoticeAlertModelStudent(String noticeTitle, String noticeDate, String studentProgram, String studentYear, String studentSemester, boolean isRead) {
        this.noticeTitle = noticeTitle;
        this.noticeDate = noticeDate;
        this.studentProgram = studentProgram;
        this.studentYear = studentYear;
        this.studentSemester = studentSemester;
        this.isRead = isRead;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getStudentProgram() {
        return studentProgram;
    }

    public void setStudentProgram(String studentProgram) {
        this.studentProgram = studentProgram;
    }

    public String getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(String studentYear) {
        this.studentYear = studentYear;
    }

    public String getStudentSemester() {
        return studentSemester;
    }

    public void setStudentSemester(String studentSemester) {
        this.studentSemester = studentSemester;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
