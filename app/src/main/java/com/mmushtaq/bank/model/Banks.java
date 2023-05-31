package com.mmushtaq.bank.model;

public class Banks {

    String name;
    String status;

    public Banks(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Banks(String name) {
        this.name = name;
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
}
