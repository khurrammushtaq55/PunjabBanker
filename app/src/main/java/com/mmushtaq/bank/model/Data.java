package com.mmushtaq.bank.model;

import java.util.ArrayList;

public class Data {

    private int id;
    private String email;
    private boolean can_upload_picture;
    private String first_name;
    private String last_name;
    private int sign_in_count;
    private String status;
    private int submitted_cases_count;
    private int pending_cases_count;
    private ArrayList<String> roles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCan_upload_picture() {
        return can_upload_picture;
    }

    public void setCan_upload_picture(boolean can_upload_picture) {
        this.can_upload_picture = can_upload_picture;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getSign_in_count() {
        return sign_in_count;
    }

    public void setSign_in_count(int sign_in_count) {
        this.sign_in_count = sign_in_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public int getSubmitted_cases_count() {
        return submitted_cases_count;
    }

    public void setSubmitted_cases_count(int submitted_cases_count) {
        this.submitted_cases_count = submitted_cases_count;
    }

    public int getPending_cases_count() {
        return pending_cases_count;
    }

    public void setPending_cases_count(int pending_cases_count) {
        this.pending_cases_count = pending_cases_count;
    }
}
