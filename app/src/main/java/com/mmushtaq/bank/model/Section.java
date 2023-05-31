package com.mmushtaq.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Section implements Serializable {

    public String type;
    public String title;
    public ArrayList<Question> questions;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
