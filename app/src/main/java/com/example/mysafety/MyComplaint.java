package com.example.mysafety;

public class MyComplaint {
    private String date;
    private String complaint;
    String time;

    public MyComplaint(){}

    public MyComplaint(String date, String complaint, String time) {
        this.date = date;
        this.complaint = complaint;
        this.time=time;
    }

    public String getDate() {
        return date;
    }

    public String getComplaint() {
        return complaint;
    }

    public String getTime() {
        return time;
    }
}
