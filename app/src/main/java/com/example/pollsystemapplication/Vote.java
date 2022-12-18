package com.example.pollsystemapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Vote implements Serializable {
    ArrayList<String> answers;
    Date time;
    CustomLocation location;

    public Vote() {
    }

    public Vote(ArrayList<String> answers, Date time, CustomLocation location) {
        this.answers = answers;
        this.time = time;
        this.location = location;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }
}
