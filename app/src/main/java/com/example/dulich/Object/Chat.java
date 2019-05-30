package com.example.dulich.Object;

import java.util.List;

public class Chat {
    private String ChatName;
    private String ChatID;
    private List<String> User;

    public Chat(String chatName, String chatID, List<String> user) {
        ChatName = chatName;
        ChatID = chatID;
        User = user;
    }

    public Chat() {

    }

    public String getChatName() {
        return ChatName;
    }

    public void setChatName(String chatName) {
        ChatName = chatName;
    }

    public String getChatID() {
        return ChatID;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }

    public List<String> getUser() {
        return User;
    }

    public void setUser(List<String> user) {
        User = user;
    }
}

