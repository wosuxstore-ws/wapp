package com.wl.orders.models;

// Simple data holder for one row of get_orders.php's "orders" array.
public class Order {
    public int id;
    public String orderNumber;
    public String status;
    public String grandTotal;
    public String shippingMethod;
    public String paymentLabel;
    public String city;
    public String province;
    public String createdAt;
}
