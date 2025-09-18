package com.example.cms;

public class StudentHomeDetailsModel {
     String name, contact, program, semester;

    public StudentHomeDetailsModel() {
    }

    public StudentHomeDetailsModel(String name, String contact, String program, String semester) {
        this.name = name;
        this.contact = contact;
        this.program = program;
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
