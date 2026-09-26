package com.wl.orders;

import android.content.Context;
import android.content.SharedPreferences;

// Tiny wrapper around SharedPreferences to remember who's logged in.
// Good enough for testing; for production, look at EncryptedSharedPreferences instead.
public class SessionManager {

    private static final String PREFS = "wl_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveUser(int userId, String fullName, String email) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getFullName() {
        return prefs.getString(KEY_NAME, "");
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
