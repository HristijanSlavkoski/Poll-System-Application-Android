package com.example.pollsystemapplication;

import java.io.Serializable;

public class CustomLocation implements Serializable {
    double longitude;
    double latitude;

    public CustomLocation() {
    }

    public CustomLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
