package com.example.shutter.Models;

public class UserSettings {
    private Users users;
    private UserAccountSettings settings;

    //constructors
    public UserSettings(Users users, UserAccountSettings settings) {
        this.users = users;
        this.settings = settings;
    }

    public UserSettings(){ }


    //getters and setters
    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public UserAccountSettings getSettings() {
        return settings;
    }

    public void setSettings(UserAccountSettings settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "users=" + users +
                ", settings=" + settings +
                '}';
    }
}
