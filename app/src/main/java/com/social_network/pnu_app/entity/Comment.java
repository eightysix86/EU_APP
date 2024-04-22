package com.social_network.pnu_app.entity;

public class Comment {

    String keySender;
    String type;
    String text;
    long time;


    String linkFirebaseStoragePostPhoto;

    Comment(){}
    public Comment(String keySender, String type, String text, long time){
        this.keySender = keySender;
        this.type = type;
        this.text = text;
        this.time = time;
    }

    public Comment(String keySender, String type, String text, long time, String linkFirebaseStoragePostPhoto){
        this.keySender = keySender;
        this.type = type;
        this.text = text;
        this.time = time;
        this.linkFirebaseStoragePostPhoto = linkFirebaseStoragePostPhoto;
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


    public String getLinkFirebaseStoragePostPhoto() {
        return linkFirebaseStoragePostPhoto;
    }

    public void setLinkFirebaseStoragePostPhoto(String linkFirebaseStoragePostPhoto) {
        this.linkFirebaseStoragePostPhoto = linkFirebaseStoragePostPhoto;
    }
}
