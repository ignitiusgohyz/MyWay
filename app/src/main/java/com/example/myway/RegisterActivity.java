package com.example.myway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//TODO
// 1. Replace all deprecated methods.
// 2. Insert autofillHints after finding out what it does.
// 3. Resize the UI layout of RegisterActivity so we can increase font size of password warning.

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private ImageButton visibilityButton;
    private EditText first_password;
    private EditText second_password;
    private EditText first_name;
    private EditText last_name;
    private EditText email;
    private TextView different_password_1;
    private TextView different_password_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        first_password = findViewById(R.id.first_password);
        second_password = findViewById(R.id.second_password);
        visibilityButton = findViewById(R.id.visibility_button);
        different_password_1 = findViewById(R.id.different_password_warning);
        different_password_2 = findViewById(R.id.different_password_warning2);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);

        visibilityButton.setOnClickListener(v -> {
            Toast toast;
            if (visibilityButton.getContentDescription().equals("Password Hidden")) {
                first_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                second_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                visibilityButton.setContentDescription("Password Visible");
                visibilityButton.setImageResource(R.drawable.ic_password_visible);
                toast = Toast.makeText(RegisterActivity.this, "Show", Toast.LENGTH_SHORT);
            } else {
                first_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                second_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                visibilityButton.setContentDescription("Password Hidden");
                visibilityButton.setImageResource(R.drawable.ic_password_not_visible);
                toast = Toast.makeText(RegisterActivity.this, "Hide", Toast.LENGTH_SHORT);
            }
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(toast::cancel, 500);
            first_password.setSelection(first_password.getText().length());
            second_password.setSelection(second_password.getText().length());
        });

        first_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        last_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        email.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);

        first_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(first_password.getText().toString().equals(second_password.getText().toString()))) {
                    first_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setVisibility(View.VISIBLE);
                    different_password_2.setVisibility(View.VISIBLE);
                } else {
                    first_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setVisibility(View.INVISIBLE);
                    different_password_2.setVisibility(View.INVISIBLE);
                }
            }
        });

        second_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(first_password.getText().toString().equals(second_password.getText().toString()))) {
                    first_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setVisibility(View.VISIBLE);
                    different_password_2.setVisibility(View.VISIBLE);
                } else {
                    first_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setVisibility(View.INVISIBLE);
                    different_password_2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}