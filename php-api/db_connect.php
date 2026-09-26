<?php
// Shared DB connection used by every endpoint.
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *'); // harmless for an app; useful if you test from a browser/Postman

require_once 'config.php';

$conn = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_NAME);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Database connection failed']);
    exit;
}

$conn->set_charset('utf8mb4');
