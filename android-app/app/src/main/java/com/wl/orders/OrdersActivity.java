package com.wl.orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.wl.orders.models.Order;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private OrdersManager ordersManager;
    private SessionManager sessionManager;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setTitle("Orders - " + sessionManager.getFullName());

        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.ordersRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(new ArrayList<>(), order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("order_id", order.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        ordersManager = new OrdersManager(this);

        swipeRefresh.setOnRefreshListener(this::loadOrders);

        loadOrders();
    }

    private void loadOrders() {
        swipeRefresh.setRefreshing(true);
        ordersManager.getOrders(sessionManager.getUserId(), new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                swipeRefresh.setRefreshing(false);
                try {
                    if (response.getBoolean("success")) {
                        JSONArray array = response.getJSONArray("orders");
                        List<Order> orders = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);
                            Order order = new Order();
                            order.id = o.getInt("id");
                            order.orderNumber = o.optString("order_number");
                            order.status = o.optString("status");
                            order.grandTotal = o.optString("grand_total");
                            order.shippingMethod = o.optString("shipping_method");
                            order.paymentLabel = o.optString("payment_method_display_label");
                            order.city = o.optString("city");
                            order.province = o.optString("province");
                            order.createdAt = o.optString("created_at");
                            orders.add(order);
                        }
                        adapter.setOrders(orders);
                        emptyView.setVisibility(orders.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                    } else {
                        Toast.makeText(OrdersActivity.this, response.optString("message", "Couldn't load orders"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(OrdersActivity.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(OrdersActivity.this, "Couldn't reach server: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
