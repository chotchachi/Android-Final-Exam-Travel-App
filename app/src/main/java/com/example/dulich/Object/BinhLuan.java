package com.example.dulich.Object;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BinhLuan {
    private String username;
    private String binhluan;
    private @ServerTimestamp
    Date timestamp;

    public BinhLuan(String username, String binhluan, Date timestamp) {
        this.username = username;
        this.binhluan = binhluan;
        this.timestamp = timestamp;
    }

    public BinhLuan(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBinhluan() {
        return binhluan;
    }

    public void setBinhluan(String binhluan) {
        this.binhluan = binhluan;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
