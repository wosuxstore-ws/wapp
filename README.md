# WL Orders — your own backend + Android app (no Firebase)

This replaces the generic "notes app" starter your other AI gave you with something
built against your **actual** database (`wl.sql`): `users`, `orders`, `order_items`.
What "your own Firebase" means here: a small PHP API sitting in front of your MySQL
database on your normal web hosting, which the Android app talks to over HTTPS.
No Firebase, no third-party backend service — you own the server and the database.

```
php-api/        Upload this folder to your hosting (e.g. public_html/api/)
android-app/    Open this folder directly in Android Studio
.github/workflows/build-apk.yml   Builds a debug APK automatically via GitHub Actions
```

## How it fits together

1. Your hosting runs MySQL (with `wl.sql` imported) + PHP (`php-api/`).
2. The Android app calls `login.php`, `get_orders.php`, `get_order_detail.php` over HTTP(S).
3. It never touches the database directly — only your PHP does. That's the whole point:
   your DB credentials live only in `config.php` on the server, never in the app.

---

## Part 1 — Deploy the PHP API

1. In cPanel (or wherever you host), create a MySQL database + user, and **import `wl.sql`**
   into it (phpMyAdmin → Import, or `mysql -u user -p dbname < wl.sql`).
2. Upload everything in `php-api/` to a folder on your server, e.g. `public_html/api/`.
3. Edit `php-api/config.php` on the server with your real `DB_SERVER` / `DB_USERNAME` /
   `DB_PASSWORD` / `DB_NAME`, and set `ADMIN_KEY` to a random string only you know.
4. Confirm `.htaccess` uploaded too (it blocks browser access to `config.php`).

### Test the API with curl/browser before touching Android at all

Your `orders` table already has 10 real rows for testing (e.g. `user_id` 13 and 22),
but you likely don't know the plaintext password for those existing accounts — they were
created by your storefront/chatbot with a hash you never see. So set a **known password**
on a real account using the one-time helper:

```
https://yourdomain.com/api/create_test_user.php?key=YOUR_ADMIN_KEY&email=EXISTING_EMAIL&password=Test1234
```

Use an email that's actually in your `users` table and has orders (id 13 or 22 in the
sample data). This only overwrites the password hash — nothing else on the account changes.

Then test login:
```bash
curl -X POST https://yourdomain.com/api/login.php \
  -H "Content-Type: application/json" \
  -d '{"email":"EXISTING_EMAIL","password":"Test1234"}'
```
Expect `{"success":true,"user":{"id":13,...}}`. Note the `id` — you'll need it next.

Test the orders list:
```bash
curl "https://yourdomain.com/api/get_orders.php?user_id=13"
```
Expect `{"success":true,"orders":[...]}`.

Test one order's detail (pick an `id` from the list above):
```bash
curl "https://yourdomain.com/api/get_order_detail.php?order_id=1&user_id=13"
```
Expect the order plus an `"items"` array.

**Once everything above returns real data, delete `create_test_user.php` from the
server** — it's a testing convenience, not something to leave live.

---

## Part 2 — Point the Android app at your API

Open `android-app/` in Android Studio (File → Open → select the `android-app` folder).

Edit `app/src/main/java/com/wl/orders/ApiConfig.java`:
- Testing on the Android **emulator** against a PHP server on your own computer
  (XAMPP/MAMP/`php -S 0.0.0.0:8000`): use `http://10.0.2.2:8000/` (that's the emulator's
  alias for your computer's localhost).
- Testing on a **real phone**, or against your live hosting: use
  `https://yourdomain.com/api/`.

If you use plain `http://` (no SSL) for local testing, `usesCleartextTraffic="true"` is
already set in `AndroidManifest.xml` so Android won't block it. Once you're on `https://`
you can remove that line (optional cleanup, not required).

### Run it in Android Studio first (fastest way to test)

1. Let Gradle sync (Android Studio will prompt to download the Gradle wrapper jar the
   first time — accept that, it's normal and only needs internet once).
2. Run the app on an emulator or plugged-in phone (the green ▶ button).
3. Log in with the email/password you set via `create_test_user.php`.
4. You should land on the Orders screen showing that user's real orders from `wl.sql`.
5. Tap an order to see its line items (from `order_items`).
6. Pull down to refresh the list.

This step (running from Android Studio) is the quickest way to confirm login + orders
actually work end-to-end before worrying about building an APK on GitHub.

---

## Part 3 — Push to GitHub and build an APK with Actions

1. Create a new GitHub repo and push this whole folder (`php-api/`, `android-app/`,
   `.github/`) to it. **Do not commit real DB credentials** — `config.php` only exists
   on your server, not in the repo (don't upload the edited version with real passwords).
2. Go to the repo's **Actions** tab. The `Build debug APK` workflow runs automatically on
   push to `main`, or click **Run workflow** to trigger it manually.
3. When it finishes (green check), open the run → **Artifacts** at the bottom →
   download `wl-orders-debug-apk`. Unzip it to get `app-debug.apk`.
4. Copy that APK to your phone (or `adb install app-debug.apk`) and install it — you may
   need to allow "install unknown apps" for whichever app you copied it with.
5. Open the app, log in, confirm you see the same orders you saw from Android Studio.

The workflow builds a **debug** APK (fine for testing/sideloading on your own phone). If
you later want to publish to the Play Store, you'd add a signing config and build a
release APK/AAB instead — a separate step, ask if you want that set up.

---

## Security notes (read before you go further with this)

- `get_order_detail.php` checks that the `order_id` actually belongs to `user_id` before
  returning anything — one account can't view another account's order by changing the
  number in the URL.
- The current login has no "remember me" token/session expiry — `SessionManager` just
  stores the user's id locally on the phone via `SharedPreferences` once logged in. Fine
  for testing; for a real release you'd want a proper auth token (e.g. JWT) instead of
  trusting a bare `user_id`, since right now anyone who can call your API directly could
  pass any `user_id` and read that user's orders without a password. Worth fixing before
  this goes further than testing on your own phone.
- Delete `create_test_user.php` from the server once you're done testing (see Part 1).
