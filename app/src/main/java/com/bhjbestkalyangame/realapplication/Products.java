package com.bhjbestkalyangame.realapplication;

public class Products {
    String ticket_token, date, validity;

    public Products(String ticket_token, String date, String validity) {
        this.ticket_token = ticket_token;
        this.date = date;
        this.validity = validity;
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

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

}
