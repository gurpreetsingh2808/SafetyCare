package com.example.gurpreetsingh.project.domain;

/**
 * Created by Gurpreet on 15-01-2017.
 */
public class Contact {

    private String name;
    private String number;

    public Contact(String contactName, String contactNumber) {
        name = contactName;
        number = contactNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
