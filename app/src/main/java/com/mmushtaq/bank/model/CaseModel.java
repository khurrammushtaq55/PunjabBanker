package com.mmushtaq.bank.model;

import java.io.Serializable;
import java.util.List;

public class CaseModel implements Serializable {

    public String status;
    public List<Case> cases;
    private int submitted_cases_count;
    private int pending_cases_count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
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
