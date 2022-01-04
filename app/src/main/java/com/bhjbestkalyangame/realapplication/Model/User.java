package com.bhjbestkalyangame.realapplication.Model;


public class User {

    private String ID, Username, Email, Status, ImageUrl, Token, newMessage;
    private long timestamp;

    public User(String ID, String Username, String Status, String Email, String ImageUrl, String Token, long timestamp, String newMessage) {
        this.ID = ID;
        this.Username = Username;
        this.Email = Email;
        this.Status = Status;
        this.ImageUrl = ImageUrl;
        this.Token = Token;
        this.timestamp = timestamp;
        this.newMessage = newMessage;

    }

    public User() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }
}