package com.social_network.pnu_app.entity;

public class Post {

    String keySender;
    String type;
    String text;
    long time;

    int countShare;



    String linkFirebaseStoragePostPhoto;

    public Post(){}
    public Post(String keySender, String type, String text, long time, int countShare){
        this.keySender = keySender;
        this.type = type;
        this.text = text;
        this.time = time;
        this.countShare = countShare;
    }

    public Post(String keySender, String type, String text, long time, String linkFirebaseStoragePostPhoto, int countShare){
        this.keySender = keySender;
        this.type = type;
        this.text = text;
        this.time = time;
        this.linkFirebaseStoragePostPhoto = linkFirebaseStoragePostPhoto;
        this.countShare = countShare;
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

    public int getCountShare() { return countShare; }

    public void setCountShare(int countShare) { this.countShare = countShare; }
}
