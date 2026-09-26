<?php
// ============================================
// DATABASE CONFIG - EDIT THESE WITH YOUR
// HOSTING / cPANEL MYSQL DATABASE DETAILS
// (cPanel > MySQL Databases to find/create these)
// ============================================
define('DB_SERVER', 'localhost');              // usually "localhost" on cPanel/shared hosting
define('DB_USERNAME', 'yourcpaneluser_dbuser'); // the MySQL user cPanel created for you
define('DB_PASSWORD', 'your_db_password');
define('DB_NAME', 'yourcpaneluser_wl');         // the database you imported wl.sql into

// A private key that only you and this server know.
// create_test_user.php checks this before touching the users table.
// Change it to something random, then delete create_test_user.php once you're done testing.
define('ADMIN_KEY', 'change-this-to-something-only-you-know');
