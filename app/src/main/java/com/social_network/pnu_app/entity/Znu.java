package com.social_network.pnu_app.entity;

public class Znu {


    String name;


    String question;

    Znu(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Znu(String name, String question) {
        this.name = name;
        this.question = question;
    }

    public Znu(String question) {
        this.question = question;
    }

  /*  public Znu(String name) {
        this.name = name;
    }
*/


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


}
