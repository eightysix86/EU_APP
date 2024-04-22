package com.social_network.pnu_app.entity;

public class RequestsFriends {

    String requestType;

    RequestsFriends(){}

    public RequestsFriends(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
