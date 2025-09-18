package com.example.cms;

public class ChatModelFaculty_AdminDetails {
    private String name , uid ;

    public ChatModelFaculty_AdminDetails() {
    }

    public ChatModelFaculty_AdminDetails(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return name ;
    }
}
