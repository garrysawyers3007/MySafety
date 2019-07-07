package com.example.mysafety;

public class Details {
    private String date;
    private String username;
    private String complaint;

    public Details(){}

    public Details(String date, String username, String complaint) {
        this.date = date;
        this.username = username;
        this.complaint = complaint;
    }

    public String getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getComplaint() {
        return complaint;
    }

}
