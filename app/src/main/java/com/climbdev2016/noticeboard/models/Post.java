package com.climbdev2016.noticeboard.models;

/**
 * Created by zwe on 4/26/17.
 */

public class Post {

    private String owner_id;
    private String user_name;
    private String user_profile_picture;
    private String time;
    private String content;
    private String category;

   public Post(){
   }

    public Post(String owner_id, String user_name, String user_profile_picture, String time, String content, String category) {
        this.owner_id = owner_id;
        this.user_name = user_name;
        this.user_profile_picture = user_profile_picture;
        this.time = time;
        this.content = content;
        this.category = category;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_picture() {
        return user_profile_picture;
    }

    public void setUser_profile_picture(String user_profile_picture) {
        this.user_profile_picture = user_profile_picture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
