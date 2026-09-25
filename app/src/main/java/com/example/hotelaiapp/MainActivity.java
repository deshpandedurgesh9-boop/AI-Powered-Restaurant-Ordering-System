package com.example.hotelaiapp;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String USER_ID = "table-1";

    private TextView tvDisplayArea, tvChatLog;
    private EditText etMessage;
    private Button btnLoadMenu, btnLoadHistory, btnSend, btnApprove, btnReject;
    private LinearLayout layoutDecision;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplayArea = findViewById(R.id.tvDisplayArea);
        tvChatLog = findViewById(R.id.tvChatLog);
        etMessage = findViewById(R.id.etMessage);
        btnLoadMenu = findViewById(R.id.btnLoadMenu);
        btnLoadHistory = findViewById(R.id.btnLoadHistory);
        btnSend = findViewById(R.id.btnSend);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        layoutDecision = findViewById(R.id.layoutDecision);

        tvDisplayArea.setMovementMethod(new ScrollingMovementMethod());
        apiService = ApiClient.getService();

        btnLoadMenu.setOnClickListener(v -> loadMenu());
        btnLoadHistory.setOnClickListener(v -> loadOrderHistory());
        btnSend.setOnClickListener(v -> sendMessage());
        btnApprove.setOnClickListener(v -> sendDecision("approve"));
        btnReject.setOnClickListener(v -> sendDecision("reject"));
    }

    private void loadMenu() {
        apiService.getMenu().enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItem> items = response.body().getMenu();
                    StringBuilder sb = new StringBuilder("--- CURRENT MENU ---\n");
                    for (MenuItem item : items) {
                        sb.append(item.getItemName())
                                .append(" - Rs. ").append(item.getPrice())
                                .append(" | Stock: ").append(item.getQuantity()).append("\n");
                    }
                    tvDisplayArea.setText(sb.toString());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load menu (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderHistory() {
        apiService.getOrderHistory().enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderHistoryItem> orders = response.body().getOrders();
                    if (orders.isEmpty()) {
                        tvDisplayArea.setText("No orders placed yet.");
                        return;
                    }
                    StringBuilder sb = new StringBuilder("--- ORDER HISTORY ---\n");
                    for (OrderHistoryItem ord : orders) {
                        sb.append("[").append(ord.getOrderNumber()).append("] ")
                                .append(ord.getItemName()).append(" x").append(ord.getQuantity())
                                .append(" - Rs. ").append(ord.getTotalPrice()).append("\n")
                                .append("Customer: ").append(ord.getCustomerName())
                                .append(" | Phone: ").append(ord.getPhoneNumber()).append("\n")
                                .append("Address: ").append(ord.getDeliveryAddress()).append("\n")
                                .append("Time: ").append(ord.getOrderTime()).append("\n\n");
                    }
                    tvDisplayArea.setText(sb.toString());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load order history (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        appendChat("You: " + msg);
        etMessage.setText("");

        apiService.sendChat(new ChatRequest(USER_ID, msg)).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse res = response.body();
                    appendChat("AI: " + res.getReply());

                    if ("confirmation_needed".equals(res.getStatus())) {
                        layoutDecision.setVisibility(View.VISIBLE);
                    }
                } else {
                    appendChat("Error: Server returned code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                appendChat("Error: " + t.getMessage());
            }
        });
    }

    private void sendDecision(String decision) {
        layoutDecision.setVisibility(View.GONE);
        appendChat("You selected: " + decision.toUpperCase());

        apiService.sendDecision(new DecisionRequest(USER_ID, decision)).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appendChat("AI: " + response.body().getReply());
                    loadMenu();
                } else {
                    appendChat("Error: Server returned code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                appendChat("Error: " + t.getMessage());
            }
        });
    }

    private void appendChat(String text) {
        tvChatLog.append("\n" + text + "\n");
    }
}