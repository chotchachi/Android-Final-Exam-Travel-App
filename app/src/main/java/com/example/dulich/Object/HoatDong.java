package com.example.dulich.Object;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class HoatDong {
    private String id;
    private String username;
    private String status;
    private @ServerTimestamp
    Date timestamp;
    private GeoPoint vitri;
    private List<String> tag_friends;
    private List<String> images;
    private List<String> likes;

    public HoatDong(String id, String username, String status, Date timestamp, GeoPoint vitri, List<String> tag_friends, List<String> images, List<String> likes) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.timestamp = timestamp;
        this.vitri = vitri;
        this.tag_friends = tag_friends;
        this.images = images;
        this.likes = likes;
    }

    public HoatDong(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public GeoPoint getVitri() {
        return vitri;
    }

    public void setVitri(GeoPoint vitri) {
        this.vitri = vitri;
    }

    public List<String> getTag_friends() {
        return tag_friends;
    }

    public void setTag_friends(List<String> tag_friends) {
        this.tag_friends = tag_friends;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }
}
