package com.bhjbestkalyangame.realapplication.Model;

public class User {

    private String ID, Username, Email, Status, ImageUrl;

    public User(String ID, String Username, String Status, String Email, String ImageUrl) {
        this.ID = ID;
        this.Username = Username;
        this.Email = Email;
        this.Status = Status;
        this.ImageUrl = ImageUrl;

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
}