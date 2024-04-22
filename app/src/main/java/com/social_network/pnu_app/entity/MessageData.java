package com.social_network.pnu_app.entity;

import java.util.Date;
import java.util.Map;

public class MessageData {

    String message;


    boolean seen;
    long time;
    String type;
    String key;

    String id;

    long Atime;

    public MessageData(long Atime){
        this.Atime = Atime;

    }

    public MessageData(String message, boolean seen, String type, String key, String id){
        this.message = message;
        this.seen = seen;
        this.time = new Date().getTime();
        this.type = type;
        this.key = key;
        this.id = id;

    }

   public MessageData(){}


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isSeen() { return seen; }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

}
