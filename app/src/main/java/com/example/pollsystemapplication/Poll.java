package com.example.pollsystemapplication;

import java.util.ArrayList;
import java.util.Date;

public class Poll {
    String title;
    String creator;
    ArrayList<Question> questions;
    Date start;
    Date end;

    public Poll() {
    }

    public Poll(String title, String creator, ArrayList<Question> questions, Date start, Date end) {
        this.title = title;
        this.creator = creator;
        this.questions = questions;
        this.start = start;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}


