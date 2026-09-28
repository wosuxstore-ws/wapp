package com.wl.orders;

public class ApiConfig {

    // TODO: point this at your own server once the PHP files are uploaded.
    //
    // - Testing on the Android EMULATOR against a PHP server running on your
    //   OWN computer (e.g. via XAMPP/MAMP/`php -S`): use 10.0.2.2, which is
    //   the emulator's special alias for "your computer's localhost":
    //       "http://10.0.2.2:8000/"
    //
    // - Testing on a REAL PHONE, or once you've uploaded to your real
    //   hosting (GoDaddy/cPanel/etc): use the real domain, with https:
    //       "https://yourdomain.com/api/"
    public static final String BASE_URL = "https://wosux.com/api/";

    public static final String LOGIN_URL            = BASE_URL + "login.php";
    public static final String GET_ORDERS_URL       = BASE_URL + "get_orders.php";
    public static final String GET_ORDER_DETAIL_URL = BASE_URL + "get_order_detail.php";
}
