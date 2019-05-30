package com.example.dulich.Object;

public class User {
    String username;
    String user_id;
    String hovaten;
    String avatar_url;
    String sinh_nhat;

    public User(String username, String user_id, String hovaten, String avatar_url, String sinh_nhat) {
        this.username = username;
        this.user_id = user_id;
        this.hovaten = hovaten;
        this.avatar_url = avatar_url;
        this.sinh_nhat = sinh_nhat;
    }

    public User(){

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHovaten() {
        return hovaten;
    }

    public void setHovaten(String hovaten) {
        this.hovaten = hovaten;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getSinh_nhat() {
        return sinh_nhat;
    }

    public void setSinh_nhat(String sinh_nhat) {
        this.sinh_nhat = sinh_nhat;
    }
}
