package com.social_network.pnu_app.entity;

public class Question {

    Question(){}



    String namePoll;

    String question;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question, String namePoll) {

        this.question = question;
        this.namePoll = namePoll;
    }

    public Question(String question) {
        this.question = question;
    }

    public String getNamePoll() { return namePoll; }

    public void setNamePoll(String namePoll) { this.namePoll = namePoll; }

}