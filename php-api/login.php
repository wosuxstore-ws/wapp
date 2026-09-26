<?php
// POST { "email": "...", "password": "..." }
// Matches the real `users` table from wl.sql (full_name, bcrypt password hash).
require_once 'db_connect.php';

$data = json_decode(file_get_contents("php://input"), true) ?: [];

$email = trim($data['email'] ?? '');
$password = $data['password'] ?? '';

if ($email === '' || $password === '') {
    echo json_encode(['success' => false, 'message' => 'Email and password are required']);
    exit;
}

$stmt = $conn->prepare("SELECT id, full_name, email, password, is_verified, points_balance FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(['success' => false, 'message' => 'Invalid email or password']);
    $stmt->close();
    $conn->close();
    exit;
}

$user = $result->fetch_assoc();

// Some accounts in this DB were created via Google sign-in only and have no
// local password hash (auth_provider = 'google'). Guard against that instead
// of throwing a PHP warning inside password_verify().
if (empty($user['password']) || !password_verify($password, $user['password'])) {
    echo json_encode(['success' => false, 'message' => 'Invalid email or password']);
    $stmt->close();
    $conn->close();
    exit;
}

unset($user['password']); // never send the hash back
echo json_encode(['success' => true, 'message' => 'Login successful', 'user' => $user]);

$stmt->close();
$conn->close();
