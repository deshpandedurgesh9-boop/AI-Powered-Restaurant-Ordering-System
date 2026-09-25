package com.example.hotelaiapp;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryResponse {
    @SerializedName(value = "orders", alternate = {"order_history", "history", "data"})
    private List<OrderHistoryItem> orders;

    public List<OrderHistoryItem> getOrders() {
        return orders != null ? orders : new ArrayList<>();
    }
}
