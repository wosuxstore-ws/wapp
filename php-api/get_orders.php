<?php
// GET get_orders.php?user_id=13
// Returns the order list (no line items) for one logged-in user, newest first.
require_once 'db_connect.php';

$user_id = intval($_GET['user_id'] ?? 0);

if ($user_id <= 0) {
    echo json_encode(['success' => false, 'message' => 'user_id is required']);
    exit;
}

$stmt = $conn->prepare(
    "SELECT id, order_number, status, grand_total, retail_total, shipping_fee,
            payment_method_display_label, shipping_method, city, province,
            created_at
     FROM orders
     WHERE user_id = ?
     ORDER BY created_at DESC"
);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];
while ($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

echo json_encode(['success' => true, 'orders' => $orders]);

$stmt->close();
$conn->close();
