package com.example.myway;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO
// 1. Replace all deprecated methods.
// 2. Insert autofillHints after finding out what it does.
// 3. Resize the UI layout of RegisterActivity so we can increase font size of password warning.
// 4. Reposition Toast messages to somewhere more visible such as the top of the UI.
// 5. Add in number in registration fields.
// 6. Place restrictions on password lengths during registration.
// 7. Ensure e-mail is truly in e-mail format.
// 8. Disallow special characters for username as well.
// 9. Lookout for areas to improve code efficiency to prevent frame skips.

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private ImageButton visibilityButton;
    private EditText username;
    private String username_string;
    private EditText first_password;
    private String first_password_string, second_password_string;
    private EditText second_password;
    private EditText first_name;
    private EditText last_name;
    private EditText email;
    private String first_name_string, last_name_string, email_string;
    private TextView different_password_1;
    private TextView different_password_2;
    private TextView special_characters_warning;
    protected HashMap<String, String> local_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.register_button);
        username = findViewById(R.id.register_username);
        first_password = findViewById(R.id.first_password);
        second_password = findViewById(R.id.second_password);
        visibilityButton = findViewById(R.id.visibility_button);
        different_password_1 = findViewById(R.id.different_password_warning);
        different_password_2 = findViewById(R.id.different_password_warning2);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        special_characters_warning = findViewById(R.id.special_character_warning);

        Intent db_intent = getIntent();
        local_database = (HashMap<String, String>) db_intent.getSerializableExtra("hashMap");

        first_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        last_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        email.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        username.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        first_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        second_password.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);

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

        first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(first_name.getText().toString());
                boolean containsSpecialChar = matcher.find();
                if (containsSpecialChar) {
                    first_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else {
                    first_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    special_characters_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(last_name.getText().toString());
                boolean containsSpecialChar = matcher.find();
                if (containsSpecialChar) {
                    last_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else {
                    last_name.getBackground().mutate().setColorFilter(ContextCompat.getColor(RegisterActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                    special_characters_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

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

        registerButton.setOnClickListener(v -> {
            username_string = username.getText().toString().toLowerCase();
            first_password_string = first_password.getText().toString();
            second_password_string = second_password.getText().toString();
            first_name_string = first_name.getText().toString();
            last_name_string = last_name.getText().toString();
            email_string = email.getText().toString();
            if (isEmpty(username_string) || isEmpty(first_password_string) || isEmpty(second_password_string) ||
                isEmpty(first_name_string) || isEmpty(last_name_string) || isEmpty(email_string)) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                if (local_database.containsKey(username_string)) {
                    Toast.makeText(RegisterActivity.this, "Username already in use!", Toast.LENGTH_SHORT).show();
                } else {
                    local_database.put(username_string, first_password_string);
                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("hashMap", local_database);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private boolean isEmpty(String string) {
        return string.length() == 0;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(RegisterActivity.this, "Back", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("hashMap", local_database);
        setResult(RESULT_OK, intent);
        finish();
    }
}