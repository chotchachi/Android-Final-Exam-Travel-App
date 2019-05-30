package com.example.dulich.Object;

import com.google.firebase.firestore.GeoPoint;

public class DiaDiem {
    private String img;
    private String ten;
    private GeoPoint vitri;

    public DiaDiem(String img, String ten, GeoPoint vitri) {
        this.img = img;
        this.ten = ten;
        this.vitri = vitri;
    }

    public DiaDiem(){

    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public GeoPoint getVitri() {
        return vitri;
    }

    public void setVitri(GeoPoint vitri) {
        this.vitri = vitri;
    }
}
