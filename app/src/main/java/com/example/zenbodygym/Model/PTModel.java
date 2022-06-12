package com.example.zenbodygym.Model;

public class PTModel {
    public String username, name;
    public PTModel(String username, String name){
        this.username = username;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
