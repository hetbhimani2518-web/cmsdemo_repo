package com.example.cms;

public class ChatModelFaculty_StudentDetails {

    String studentId, name, program, year, semester, division;

    public ChatModelFaculty_StudentDetails() {
    }

    public ChatModelFaculty_StudentDetails(String studentId, String name, String program, String year, String semester, String division) {
        this.studentId = studentId;
        this.name = name;
        this.program = program;
        this.year = year;
        this.semester = semester;
        this.division = division;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
