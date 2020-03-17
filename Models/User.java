package com.rubenmimoun.beerchallenge.Models;

import java.util.ArrayList;

public class User {

    private String id ;
    private String name ;
    private String email;
    private String password ;
    private String latitude;
    private String longitude;
    private String challenge ;
    private String status ;
    private String imageURL ;
    private String playing;
    private String bars ;
    private String connected_to;
    private String inGame ;
    private String left;
    private boolean first ;
    private ArrayList<Places> selected_bars ;


    public User (){}


    public User(String name, String email, String password, String latitude, String longitude, String imageURL, String status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL ;
        this.challenge = "" ;
        this.id = "" ;
        this.status = status;
        this.playing = "" ;
        this.bars = "";
        this.connected_to ="" ;
        this.inGame="";
        this.left ="" ;
        this.first = false ;
        this.selected_bars = null ;
    }

    public ArrayList<Places> getSelected_bars() {
        return selected_bars;
    }

    public void setSelected_bars(ArrayList<Places> selected_bars) {
        this.selected_bars = selected_bars;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChallenge() {
        return challenge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPlaying() {
        return playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String getBars() {
        return bars;
    }

    public void setBars(String bars) {
        this.bars = bars;
    }

    public String getConnected_to() {
        return connected_to;
    }

    public String getInGame() {
        return inGame;
    }

    public void setInGame(String inGame) {
        this.inGame = inGame;
    }

    public void setConnected_to(String connected_to) {
        this.connected_to = connected_to;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(){
        first = true ;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", challenge='" + challenge + '\'' +
                ", status='" + status + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", playing='" + playing + '\'' +
                ", bars='" + bars + '\'' +
                ", connected_to='" + connected_to + '\'' +
                '}';
    }
}
