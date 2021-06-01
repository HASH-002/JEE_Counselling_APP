package com.company.jeecounselling_choosethebest.model;

import java.util.Date;

public class BlogPost extends BlogPostId {

    //BlogPost is a class that is used as data structure for the implementation of the posting posts.

    //String and Date classes used for getting and setting
    public String imageUrl, desc, id, username;
    public Date timestamp;


    //Empty Constructor
    public BlogPost() {

    }

    public BlogPost(String image_url, String desc, String id, String username, Date timestamp) {
        this.imageUrl = image_url;
        this.desc = desc;
        this.id = id;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}