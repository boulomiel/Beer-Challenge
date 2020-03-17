package com.rubenmimoun.beerchallenge.Models;

public class Tracking {

    private String uid ;
    private String lat ;
    private String lon ;
    private String email ;

    public Tracking() {

    }

    public Tracking(String uid, String lat, String lon) {
        this.uid = uid;
        this.lat = lat;
        this.lon = lon;
    }

    public Tracking(String uid, String lat, String lon, String email) {
        this.uid = uid;
        this.lat = lat;
        this.lon = lon;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
