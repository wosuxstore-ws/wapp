package com.wl.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;
    private AuthManager authManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        progressBar = findViewById(R.id.progressBar);
        Button loginButton = findViewById(R.id.loginButton);

        authManager = new AuthManager(this);
        sessionManager = new SessionManager(this);

        // Already logged in from a previous run? skip straight to the orders list.
        if (sessionManager.isLoggedIn()) {
            goToOrders();
        }

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        authManager.login(email, password, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                setLoading(false);
                try {
                    if (response.getBoolean("success")) {
                        JSONObject user = response.getJSONObject("user");
                        sessionManager.saveUser(
                                user.getInt("id"),
                                user.optString("full_name", ""),
                                user.optString("email", "")
                        );
                        goToOrders();
                    } else {
                        Toast.makeText(LoginActivity.this, response.optString("message", "Login failed"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Couldn't reach server: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToOrders() {
        startActivity(new Intent(this, OrdersActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
