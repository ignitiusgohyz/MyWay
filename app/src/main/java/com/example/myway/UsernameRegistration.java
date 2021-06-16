package com.example.myway;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameRegistration extends AppCompatActivity {

    private EditText username;
    private TextView special_characters_warning;
    private String username_string;
    private boolean canProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_registration);

        ImageButton nextButton = findViewById(R.id.next_button);
        username = findViewById(R.id.register_username);
        ImageView usernameBox = findViewById(R.id.registration_username_box);
        TextView next = findViewById(R.id.next);
        ImageView userIcon = findViewById(R.id.user_icon);
        special_characters_warning = findViewById(R.id.special_character_warning);
        canProceed = false;

        nextButton.setTranslationX(300);
        username.setTranslationX(300);
        usernameBox.setTranslationX(300);
        next.setTranslationX(300);
        userIcon.setTranslationX(300);
        special_characters_warning.setTranslationX(300);
        float v1 = 0;
        nextButton.setAlpha(v1);
        username.setAlpha(v1);
        usernameBox.setAlpha(v1);
        next.setAlpha(v1);
        userIcon.setAlpha(v1);
        special_characters_warning.setAlpha(v1);
        nextButton.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        username.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        usernameBox.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        next.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        userIcon.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        special_characters_warning.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();


        nextButton.setOnClickListener(v -> {
            if (canProceed) {
                DatabaseHelper databaseHelper = new DatabaseHelper(UsernameRegistration.this);
                username_string = username.getText().toString().toLowerCase();
                if (databaseHelper.usernameExists(username_string)) {
                    Toast.makeText(UsernameRegistration.this, "Username already in use!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(UsernameRegistration.this, EmailRegistration.class);
                    intent.putExtra("username", username_string);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            } else {
                Toast.makeText(UsernameRegistration.this, "Please enter a proper username", Toast.LENGTH_SHORT).show();
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern pattern = Pattern.compile("[^a-z0-9._]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(username.getText().toString());
                boolean containsSpecialChar = matcher.find();
                canProceed = false;
                if (containsSpecialChar) {
                    username.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    special_characters_warning.setText(R.string.special_character_warning);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else if (username.getText().toString().length() > 30) {
                    username.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    special_characters_warning.setText(R.string.username_too_long);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else if (username.getText().toString().length() < 6) {
                    username.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    special_characters_warning.setText(R.string.username_too_short);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else {
                    username.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    special_characters_warning.setVisibility(View.INVISIBLE);
                    canProceed = true;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm").setMessage("Are you sure?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}