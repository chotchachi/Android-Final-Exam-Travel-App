package com.example.dulich.Object;

public class Avatar {
    String avatar_url;

    public Avatar(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Avatar(){

    }
    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
