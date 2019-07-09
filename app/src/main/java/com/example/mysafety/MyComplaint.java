package com.example.mysafety;

public class MyComplaint {
    private String date;
    private String complaint;
    private String time;
    private String department;


    public MyComplaint(){}

    public MyComplaint(String date, String complaint, String time,String department) {
        this.date = date;
        this.complaint = complaint;
        this.time=time;
        this.department=department;
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

    public String getDepartment() {
        return department;
    }
}
