package com.example.hotelaiapp;

public class DecisionRequest {
    private String user_id;
    private String decision;

    public DecisionRequest(String userId, String decision) {
        this.user_id = userId;
        this.decision = decision;
    }
}