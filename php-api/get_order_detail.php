<?php
// GET get_order_detail.php?order_id=1&user_id=13
// user_id is required and checked against the order's owner so one account
// can never pull up another account's order by guessing order_id.
require_once 'db_connect.php';

$order_id = intval($_GET['order_id'] ?? 0);
$user_id  = intval($_GET['user_id'] ?? 0);

if ($order_id <= 0 || $user_id <= 0) {
    echo json_encode(['success' => false, 'message' => 'order_id and user_id are required']);
    exit;
}

$stmt = $conn->prepare("SELECT * FROM orders WHERE id = ? AND user_id = ?");
$stmt->bind_param("ii", $order_id, $user_id);
$stmt->execute();
$order = $stmt->get_result()->fetch_assoc();
$stmt->close();

if (!$order) {
    echo json_encode(['success' => false, 'message' => 'Order not found']);
    $conn->close();
    exit;
}

$stmt = $conn->prepare(
    "SELECT product_id, product_name, product_image, unit_price, original_price, quantity
     FROM order_items
     WHERE order_id = ?"
);
$stmt->bind_param("i", $order_id);
$stmt->execute();
$result = $stmt->get_result();

$items = [];
while ($row = $result->fetch_assoc()) {
    $items[] = $row;
}
$order['items'] = $items;

echo json_encode(['success' => true, 'order' => $order]);

$stmt->close();
$conn->close();
