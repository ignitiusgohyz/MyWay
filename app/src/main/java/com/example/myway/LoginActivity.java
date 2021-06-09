package com.example.myway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

    private ImageButton visibilityButton;
    private EditText createdUsername;
    private String createdUsername_string;
    private EditText createdPassword;
    private String createdPassword_string;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageButton loginButton = findViewById(R.id.login);
        createdUsername = findViewById(R.id.username);
        createdPassword = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMe);
        ImageButton registerButton = findViewById(R.id.register);
        Button tempClearDatabaseButton = findViewById(R.id.tempclearbutton);
        visibilityButton = findViewById(R.id.visibility_button);
        DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);

        loginButton.setOnClickListener(v -> {
            createdUsername_string = createdUsername.getText().toString().toLowerCase().trim();
            createdPassword_string = createdPassword.getText().toString().trim();

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

        rememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","true");
                editor.apply();
            } else if(!buttonView.isChecked()) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
            }
        });

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Toast.makeText(this, "Please sign in.", Toast.LENGTH_SHORT).show();
        }

        tempClearDatabaseButton.setOnClickListener(v -> {
                databaseHelper.clearDatabase();
                Toast.makeText(LoginActivity.this, "All records deleted", Toast.LENGTH_SHORT).show();
                });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, UsernameRegistration.class);
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