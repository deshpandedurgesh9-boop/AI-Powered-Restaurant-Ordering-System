package com.example.hotelaiapp;

public class ChatRequest {
    private String user_id;
    private String message;

    public ChatRequest(String userId, String message) {
        this.user_id = userId;
        this.message = message;
    }
}