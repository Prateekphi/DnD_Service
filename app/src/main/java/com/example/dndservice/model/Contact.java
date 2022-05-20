package com.example.dndservice.model;

public class Contact {
    private int id;
    private String name;
    private String status;
    private String dateRec;
    private String dateReport;
    public Contact(String name, String status, String dateRec, String dateReport) {
        this.name = name;
        this.status = status;
        this.dateRec = dateRec;
        this.dateReport = dateReport;
    }

    public Contact(int id, String name, String status, String dateRec, String dateReport) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.dateRec = dateRec;
        this.dateReport = dateReport;
    }

    public Contact() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateRec() {
        return dateRec;
    }

    public void setDateRec(String dateRec) {
        this.dateRec = dateRec;
    }

    public String getDateReport() {
        return dateReport;
    }

    public void setDateReport(String dateReport) {
        this.dateReport = dateReport;
    }
}
