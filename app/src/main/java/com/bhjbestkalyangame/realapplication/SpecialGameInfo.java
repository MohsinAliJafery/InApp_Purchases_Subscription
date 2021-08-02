package com.bhjbestkalyangame.realapplication;

public class SpecialGameInfo {
    String display, title, type;

    public SpecialGameInfo(String display, String title, String type) {
        this.display = display;
        this.title = title;
        this.type = type;
    }

    public SpecialGameInfo() {
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
