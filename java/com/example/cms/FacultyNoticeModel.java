package com.example.cms;

public class FacultyNoticeModel {
    private String title, content, date, facultyName, facultyID , year, semester, program , division , noticeId;
    private String autoDeleteDate;
    private long timestamp; // Add this for sorting

    public FacultyNoticeModel() {
    }

    public FacultyNoticeModel(String title, String content, String date, String facultyName, String facultyID, String year, String semester, String program, String division , long timestamp , String noticeId , String autoDeleteDate) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.facultyName = facultyName;
        this.facultyID = facultyID;
        this.year = year;
        this.semester = semester;
        this.program = program;
        this.division = division;
        this.timestamp = timestamp;
        this.noticeId = noticeId;
        this.autoDeleteDate = autoDeleteDate;
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

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}


//=============For simple notice feature =================
//
//private String noticeID;
//private String facultyName;
//private String date;
//private String title;
//private String content;
//private String program;
//private String year;
//private String semester;
//private String division;
//private String facultyID;
//
//public FacultyNoticeModel() {
//}
//
//public FacultyNoticeModel(String noticeID, String facultyName, String date, String title, String content, String program, String year, String semester , String division , String facultyID) {
//    this.noticeID = noticeID;
//    this.facultyName = facultyName;
//    this.date = date;
//    this.title = title;
//    this.content = content;
//    this.program = program;
//    this.year = year;
//    this.semester = semester;
//    this.division= division;
//    this.facultyID = facultyID;
//}
//
//public String getFacultyID() {
//    return facultyID;
//}
//
//public void setFacultyID(String facultyID) {
//    this.facultyID = facultyID;
//}
//
//public String getNoticeID() {
//    return noticeID;
//}
//
//public void setNoticeID(String noticeID) {
//    this.noticeID = noticeID;
//}
//
//public String getFacultyName() {
//    return facultyName;
//}
//
//public void setFacultyName(String facultyName) {
//    this.facultyName = facultyName;
//}
//
//public String getDate() {
//    return date;
//}
//
//public void setDate(String date) {
//    this.date = date;
//}
//
//public String getTitle() {
//    return title;
//}
//
//public void setTitle(String title) {
//    this.title = title;
//}
//
//public String getContent() {
//    return content;
//}
//
//public void setContent(String content) {
//    this.content = content;
//}
//
//public String getProgram() {
//    return program;
//}
//
//public void setProgram(String program) {
//    this.program = program;
//}
//
//public String getDivision() {
//    return division;
//}
//
//public void setDivision(String division) {
//    this.division = division;
//}
//
//public String getYear() {
//    return year;
//}
//
//public void setYear(String year) {
//    this.year = year;
//}
//
//public String getSemester() {
//    return semester;
//}
//
//public void setSemester(String semester) {
//    this.semester = semester;
//}