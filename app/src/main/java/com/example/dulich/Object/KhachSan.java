package com.example.dulich.Object;

public class KhachSan {
    String Anh;
    String Ten;
    String DiaChi;
    String Link;

    public KhachSan(String anh, String ten, String diaChi, String link) {
        Anh = anh;
        Ten = ten;
        DiaChi = diaChi;
        Link = link;
    }

    public String getAnh() {
        return Anh;
    }

    public void setAnh(String anh) {
        Anh = anh;
    }

    public String getTen() {
        return Ten;
    }

    public void setTen(String ten) {
        Ten = ten;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
