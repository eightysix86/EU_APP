package com.social_network.pnu_app.entity;

public class AllAuthUsers {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public String getSeriesIDcard() {
        return seriesIDcard;
    }

    public void setSeriesIDcard(String seriesIDcard) {
        this.seriesIDcard = seriesIDcard;
    }

    public String seriesIDcard;
    public String name;
    public String lastName;
    public String group;
    public String linkFirebaseStorageMainPhoto;

    public String getLinkFirebaseStorageMainPhoto() {
        return linkFirebaseStorageMainPhoto;
    }

    public void setLinkFirebaseStorageMainPhoto(String linkFirebaseStorageMainPhoto) {
        this.linkFirebaseStorageMainPhoto = linkFirebaseStorageMainPhoto;
    }


    public AllAuthUsers(){}

    public AllAuthUsers(String studentName, String studentLastName, String studentGroup ,String studentImage, String seriesIDcard){
        this.name = studentName;
        this.lastName = studentLastName;
        this.group = studentGroup;
        this.linkFirebaseStorageMainPhoto = studentImage;
        this.seriesIDcard = seriesIDcard;
    }
}