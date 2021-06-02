package com.company.jeecounselling_choosethebest.model;

import java.util.Date;

public class Comments {
    private String message, id;
    private Date timestamp;

    public Comments(){
    }

    public Comments(String message, String id, Date timestamp) {
        this.message = message;
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
