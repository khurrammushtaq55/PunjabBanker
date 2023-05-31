package com.mmushtaq.bank.model;

import java.io.Serializable;

public class Answer implements Serializable {

    public int id;
    public String description;
    public String remarks;
    public boolean remarks_required;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isRemarks_required() {
        return remarks_required;
    }

    public void setRemarks_required(boolean remarks_required) {
        this.remarks_required = remarks_required;
    }
}
