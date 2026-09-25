package com.example.hotelaiapp;

public class ChatResponse {
    private String response;
    private String message;
    private String reply;
    private String status;

    public String getResponse() {
        if (response != null) return response;
        if (message != null) return message;
        return reply;
    }

    public String getMessage() {
        if (message != null) return message;
        if (response != null) return response;
        return reply;
    }

    public String getReply() {
        if (reply != null) return reply;
        if (response != null) return response;
        return message;
    }

    public String getStatus() {
        return status;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
