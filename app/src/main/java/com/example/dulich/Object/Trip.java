package com.example.dulich.Object;

import java.util.List;

public class Trip {
    private String TripID;
    private String UserName;
    private String TenTrip;
    private Place DiaDiem;
    private String ThoiGian;
    private String Anh;
    private List<String> ThanhVien;

    public Trip(String tripID, String userName, String tenTrip, Place diaDiem, String thoiGian, String anh, List<String> thanhVien) {
        TripID = tripID;
        UserName = userName;
        TenTrip = tenTrip;
        DiaDiem = diaDiem;
        ThoiGian = thoiGian;
        Anh = anh;
        ThanhVien = thanhVien;
    }

    public Trip(){
        //Nhận data từ Firebase
    }

    public String getTripID() {
        return TripID;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getTenTrip() {
        return TenTrip;
    }

    public void setTenTrip(String tenTrip) {
        TenTrip = tenTrip;
    }

    public Place getDiaDiem() {
        return DiaDiem;
    }

    public void setDiaDiem(Place diaDiem) {
        DiaDiem = diaDiem;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }

    public String getAnh() {
        return Anh;
    }

    public void setAnh(String anh) {
        Anh = anh;
    }

    public List<String> getThanhVien() {
        return ThanhVien;
    }

    public void setThanhVien(List<String> thanhVien) {
        ThanhVien = thanhVien;
    }
}
