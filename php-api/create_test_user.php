<?php
// ONE-TIME TESTING HELPER - DELETE THIS FILE FROM YOUR SERVER once you're done.
//
// You already have real users in the `users` table, but their password
// hashes were set by your storefront/chatbot, so you don't know the
// plaintext. This script lets you set a KNOWN password on one account so
// you can actually log in from the Android app while testing.
//
// Usage (in a browser or curl), all three params required:
//   create_test_user.php?key=YOUR_ADMIN_KEY&email=someone@example.com&password=Test1234
//
// - If that email already exists in `users`, it just updates the password.
// - If it doesn't exist, it creates a bare-bones account with that email.
// key must match ADMIN_KEY in config.php.

require_once 'db_connect.php';

$key = $_GET['key'] ?? '';
if (!hash_equals(ADMIN_KEY, $key)) {
    http_response_code(403);
    echo json_encode(['success' => false, 'message' => 'Bad or missing key']);
    exit;
}

$email = trim($_GET['email'] ?? '');
$password = $_GET['password'] ?? '';

if ($email === '' || strlen($password) < 6) {
    echo json_encode(['success' => false, 'message' => 'email and a password of 6+ chars are required']);
    exit;
}

$hash = password_hash($password, PASSWORD_DEFAULT);

$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$existing = $stmt->get_result()->fetch_assoc();
$stmt->close();

if ($existing) {
    $stmt = $conn->prepare("UPDATE users SET password = ?, is_verified = 1 WHERE email = ?");
    $stmt->bind_param("ss", $hash, $email);
    $stmt->execute();
    $stmt->close();
    echo json_encode(['success' => true, 'message' => 'Password updated for existing user', 'user_id' => $existing['id']]);
} else {
    $stmt = $conn->prepare("INSERT INTO users (email, password, full_name, is_verified) VALUES (?, ?, ?, 1)");
    $name = explode('@', $email)[0];
    $stmt->bind_param("sss", $email, $hash, $name);
    $stmt->execute();
    $newId = $stmt->insert_id;
    $stmt->close();
    echo json_encode(['success' => true, 'message' => 'Test user created', 'user_id' => $newId]);
}

$conn->close();
