package com.test.samples;

public class User {
    private String id;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String id, String name) {

        this.id = id;
        this.name = name;
    }
}
