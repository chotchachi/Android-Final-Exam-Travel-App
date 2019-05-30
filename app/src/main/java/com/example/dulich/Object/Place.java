package com.example.dulich.Object;

public class Place {
    private String img;
    private String key;
    private String ten;

    public Place(String img, String key, String ten) {
        this.img = img;
        this.key = key;
        this.ten = ten;
    }

    public Place(){

    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }
}
