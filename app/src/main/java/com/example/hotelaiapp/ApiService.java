package com.example.hotelaiapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("/menu")
    Call<MenuResponse> getMenu();

    @GET("/orders")
    Call<OrderHistoryResponse> getOrderHistory();

    @GET("/order_history")
    Call<OrderHistoryResponse> getOrderHistoryAlternate();

    @POST("/chat")
    Call<ChatResponse> sendChat(@Body ChatRequest request);

    @POST("/confirm_order")
    Call<ChatResponse> sendDecision(@Body DecisionRequest request);
}