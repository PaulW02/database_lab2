package com.example.database_lab1.model;

import org.w3c.dom.ls.LSOutput;

public class User {

    private int userId;

    private String name;

    private String username;

    private String password;

    public User(int userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String name, String username, String password) {this(-1,name,username,password);}
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return name + ", ";
    }
}
