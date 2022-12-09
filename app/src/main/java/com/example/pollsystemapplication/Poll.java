package com.example.pollsystemapplication;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Poll {
    String title;
    String creator;
    ArrayList<Question> questions;
    LocalDateTime start;
    LocalDateTime end;

    public Poll() {
    }

    public Poll(String title, String creator, ArrayList<Question> questions, LocalDateTime start, LocalDateTime end) {
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

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}


