package com.bhjbestkalyangame.realapplication;

public class SpecialGameInfo {
    String display, title, type, name;

    public SpecialGameInfo(String display, String title, String type, String name) {
        this.display = display;
        this.title = title;
        this.type = type;
        this.name = name;
    }

    public SpecialGameInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
