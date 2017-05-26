package com.climbdev2016.noticeboard.models;

/**
 * Created by emrys on 5/18/17.
 */

public class User {

    private String image;
    private String name;
    private String occupation;

    public User() {
    }

    public User(String image, String name, String occupation) {
        this.image = image;
        this.name = name;
        this.occupation = occupation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
