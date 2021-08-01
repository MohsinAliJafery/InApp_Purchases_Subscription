package com.bhjbestkalyangame.realapplication;

public class Upload {

    private String Title;
    private String Description;
    private String ImageUrl;
    public Upload() {
        //empty constructor needed
    }
    public Upload(String title, String description, String imageUrl) {

        Title = title;
        Description = description;
        ImageUrl = imageUrl;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
