package com.example.dulich.Object;

public class BanBe {
    private String Username;
    private String User_Id;

    public BanBe(String username, String user_id) {
        Username = username;
        User_Id = user_id;
    }

    public BanBe(){

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }
}
