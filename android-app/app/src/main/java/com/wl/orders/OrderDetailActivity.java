package com.wl.orders;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderNumberView, statusView, totalView, addressView;
    private LinearLayout itemsContainer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderNumberView = findViewById(R.id.detailOrderNumber);
        statusView = findViewById(R.id.detailStatus);
        totalView = findViewById(R.id.detailTotal);
        addressView = findViewById(R.id.detailAddress);
        itemsContainer = findViewById(R.id.itemsContainer);
        progressBar = findViewById(R.id.detailProgressBar);

        int orderId = getIntent().getIntExtra("order_id", -1);
        SessionManager sessionManager = new SessionManager(this);

        if (orderId <= 0 || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Missing order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        new OrdersManager(this).getOrderDetail(orderId, sessionManager.getUserId(), new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    if (!response.getBoolean("success")) {
                        Toast.makeText(OrderDetailActivity.this, response.optString("message", "Order not found"), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    JSONObject order = response.getJSONObject("order");
                    orderNumberView.setText(order.optString("order_number"));
                    statusView.setText(order.optString("status"));
                    totalView.setText("Total: Rs. " + order.optString("grand_total"));
                    addressView.setText(order.optString("recipient_name") + "\n"
                            + order.optString("address_line1") + "\n"
                            + order.optString("city") + ", " + order.optString("province") + " " + order.optString("postal_code")
                            + "\nPhone: " + order.optString("phone"));

                    JSONArray items = order.getJSONArray("items");
                    itemsContainer.removeAllViews();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        TextView row = new TextView(OrderDetailActivity.this);
                        row.setPadding(0, 16, 0, 16);
                        row.setText(item.optString("quantity") + " x " + item.optString("product_name")
                                + "  -  Rs. " + item.optString("unit_price"));
                        itemsContainer.addView(row);
                    }
                } catch (JSONException e) {
                    Toast.makeText(OrderDetailActivity.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Couldn't reach server: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
