package com.bhjbestkalyangame.realapplication;

public class Products {
    String ticket_token, date, name, email;


    public Products(String ticket_token, String date, String name, String email) {
        this.ticket_token = ticket_token;
        this.date = date;
        this.name = name;
        this.email = email;
    }

    public Products() {
    }

    public String getTicket_token() {
        return ticket_token;
    }

    public void setTicket_token(String ticket_token) {
        this.ticket_token = ticket_token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
