package com.example.cms;

public class admin_stud_view_model {
    private String name, contact, studentId;

    public admin_stud_view_model(String name, String studentId, String contact) {
        this.name = name;
        this.studentId = studentId;
        this.contact = contact;
    }

    public admin_stud_view_model() {
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
