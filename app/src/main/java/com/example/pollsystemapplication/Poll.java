package com.example.pollsystemapplication;

import java.util.ArrayList;

public class Poll {
    String title;
    String creatorId;
    ArrayList<Question> questions;
    Boolean active;

    public Poll() {
    }

    public Poll(String title, String creatorId, ArrayList<Question> questions, Boolean active) {
        this.title = title;
        this.creatorId = creatorId;
        this.questions = questions;
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}


