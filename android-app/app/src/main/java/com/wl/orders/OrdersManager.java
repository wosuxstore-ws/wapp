package com.wl.orders;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class OrdersManager {

    private final Context context;

    public OrdersManager(Context context) {
        this.context = context;
    }

    public void getOrders(int userId, ApiCallback callback) {
        String url = ApiConfig.GET_ORDERS_URL + "?user_id=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                callback::onSuccess, error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getOrderDetail(int orderId, int userId, ApiCallback callback) {
        String url = ApiConfig.GET_ORDER_DETAIL_URL + "?order_id=" + orderId + "&user_id=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                callback::onSuccess, error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private String parseError(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            return new String(error.networkResponse.data);
        }
        return error.getMessage() != null ? error.getMessage() : "Network error";
    }
}
