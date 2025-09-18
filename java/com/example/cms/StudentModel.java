package com.example.cms;

public class StudentModel {
    private String name, contact, address, dob, email, gender;

    public StudentModel() {}

    // Constructor with parameters
    public StudentModel(String name, String contact, String address, String dob, String email, String gender) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
    }

    // Getters
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }
    public String getDob() { return dob; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
}
