package com.example.cms;

public class admin_fac_view_model {
    private String name;
    private String designation , contact;

    public admin_fac_view_model() {
    }

    public admin_fac_view_model(String name, String designation, String contact) {
        this.name = name;
        this.designation = designation;
        this.contact = contact;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
