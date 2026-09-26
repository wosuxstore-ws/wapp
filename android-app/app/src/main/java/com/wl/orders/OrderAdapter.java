package com.wl.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wl.orders.models.Order;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public interface OnOrderClick {
        void onClick(Order order);
    }

    private List<Order> orders;
    private final OnOrderClick onOrderClick;

    public OrderAdapter(List<Order> orders, OnOrderClick onOrderClick) {
        this.orders = orders;
        this.onOrderClick = onOrderClick;
    }

    public void setOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderNumber.setText(order.orderNumber);
        holder.status.setText(order.status);
        holder.total.setText("Rs. " + order.grandTotal);
        holder.meta.setText(order.city + ", " + order.province + "  •  " + order.shippingMethod);
        holder.itemView.setOnClickListener(v -> onOrderClick.onClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, status, total, meta;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            status = itemView.findViewById(R.id.orderStatus);
            total = itemView.findViewById(R.id.orderTotal);
            meta = itemView.findViewById(R.id.orderMeta);
        }
    }
}
