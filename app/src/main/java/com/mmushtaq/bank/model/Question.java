package com.mmushtaq.bank.model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private String description;
    private String question_type;
    private String given_answer;
    private int id;
    private String selectedAnswer;
    private String answerId;
    private boolean mandatory = false;
    private String keyboard_type;
    private boolean isFieldFilled = false;
    private int max_length ;
    private int min_length ;

    public String getKeyboard_type() {
        return keyboard_type;
    }

    public void setKeyboard_type(String keyboard_type) {
        this.keyboard_type = keyboard_type;
    }

    public boolean isFieldFilled() {
        return isFieldFilled;
    }

    public void setFieldFilled(boolean fieldFilled) {
        isFieldFilled = fieldFilled;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<Answer> answers;

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getMax_length() {
        return max_length;
    }

    public void setMax_length(int max_length) {
        this.max_length = max_length;
    }

    public int getMin_length() {
        return min_length;
    }

    public void setMin_length(int min_length) {
        this.min_length = min_length;
    }

    public String getGiven_answer() {
        return given_answer;
    }

    public void setGiven_answer(String given_answer) {
        this.given_answer = given_answer;
    }
}
