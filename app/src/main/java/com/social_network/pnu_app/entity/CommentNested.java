package com.social_network.pnu_app.entity;

public class CommentNested {

    String keySender;
    String type;
    String text;
    long time;

    CommentNested(){}
    public CommentNested(String keySender, String type, String text, long time){
        this.keySender = keySender;
        this.type = type;
        this.text = text;
        this.time = time;
    }

    public String getKeySender() {
        return keySender;
    }

    public void setKeySender(String keySender) {
        this.keySender = keySender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
