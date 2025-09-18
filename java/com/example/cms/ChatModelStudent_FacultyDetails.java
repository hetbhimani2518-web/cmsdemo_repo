package com.example.cms;

public class ChatModelStudent_FacultyDetails {
    private String name;
    private String designation;
    private String uid;

    public ChatModelStudent_FacultyDetails() {
    }

    public ChatModelStudent_FacultyDetails(String name, String designation, String uid) {
        this.name = name;
        this.designation = designation;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        // This is how it will appear in the Spinner
        return name + " - " + designation;
    }
}
