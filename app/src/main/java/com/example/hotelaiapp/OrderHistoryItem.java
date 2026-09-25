package com.example.hotelaiapp;

import com.google.gson.annotations.SerializedName;

public class OrderHistoryItem {
    @SerializedName(value = "order_number", alternate = {"orderNumber", "order_id", "orderId"})
    private String orderNumber;

    @SerializedName(value = "item_name", alternate = {"itemName", "item", "name"})
    private String itemName;

    private int quantity;

    @SerializedName(value = "total_price", alternate = {"totalPrice", "price", "total"})
    private double totalPrice;

    @SerializedName(value = "customer_name", alternate = {"customerName", "customer"})
    private String customerName;

    @SerializedName(value = "phone_number", alternate = {"phoneNumber", "phone"})
    private String phoneNumber;

    @SerializedName(value = "delivery_address", alternate = {"deliveryAddress", "address"})
    private String deliveryAddress;

    @SerializedName(value = "order_time", alternate = {"orderTime", "time", "date"})
    private String orderTime;

    public String getOrderNumber() {
        return orderNumber != null ? orderNumber : "";
    }

    public String getItemName() {
        return itemName != null ? itemName : "";
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getCustomerName() {
        return customerName != null ? customerName : "";
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : "";
    }

    public String getDeliveryAddress() {
        return deliveryAddress != null ? deliveryAddress : "";
    }

    public String getOrderTime() {
        return orderTime != null ? orderTime : "";
    }
}
