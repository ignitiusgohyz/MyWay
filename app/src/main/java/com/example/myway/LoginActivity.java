package com.example.myway;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

    private ImageButton visibilityButton;
    private EditText createdUsername;
    private String createdUsername_string;
    private EditText createdPassword;
    private String createdPassword_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.login);
        createdUsername = findViewById(R.id.username);
        createdPassword = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.register);
        Button tempClearDatabaseButton = findViewById(R.id.tempclearbutton);
        visibilityButton = findViewById(R.id.visibility_button);
        DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);

        loginButton.setOnClickListener(v -> {
            createdUsername_string = createdUsername.getText().toString().toLowerCase();
            createdPassword_string = createdPassword.getText().toString();

            if (createdUsername_string.length() == 0 || createdPassword_string.length() == 0) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.usernameExists(createdUsername_string)) {
                boolean authenticity = databaseHelper.verifyPassword(createdUsername_string, createdPassword_string);
                if (authenticity) {
                    Toast.makeText(LoginActivity.this, "Logging In...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "No such username!", Toast.LENGTH_SHORT).show();
            }
        });

        tempClearDatabaseButton.setOnClickListener(v -> {
                databaseHelper.clearDatabase();
                Toast.makeText(LoginActivity.this, "All records deleted", Toast.LENGTH_SHORT).show();
                });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        visibilityButton.setOnClickListener(v -> {
            Toast toast;
            if (visibilityButton.getContentDescription().equals("Password Hidden")) {
                createdPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                visibilityButton.setContentDescription("Password Visible");
                visibilityButton.setImageResource(R.drawable.ic_password_visible);
                toast = Toast.makeText(LoginActivity.this, "Show", Toast.LENGTH_SHORT);
            } else {
                createdPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                visibilityButton.setContentDescription("Password Hidden");
                visibilityButton.setImageResource(R.drawable.ic_password_not_visible);
                toast = Toast.makeText(LoginActivity.this, "Hide", Toast.LENGTH_SHORT);
            }
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(toast::cancel, 500);
            createdPassword.setSelection(createdPassword.getText().length());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        createdPassword.setText("");
        createdUsername.setText("");
    }
}