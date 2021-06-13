package com.example.myway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameRegistration extends AppCompatActivity {

    private ImageView usernameBox;
    private EditText username;
    private ImageButton nextButton;
    private TextView next;
    private ImageView userIcon;
    private TextView special_characters_warning;
    private String username_string;
    private boolean canProceed;
    private float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_registration);

        nextButton = findViewById(R.id.next_button);
        username = findViewById(R.id.register_username);
        usernameBox = findViewById(R.id.registration_username_box);
        next = findViewById(R.id.next);
        userIcon = findViewById(R.id.user_icon);
        special_characters_warning = findViewById(R.id.special_character_warning);
        canProceed = false;

        nextButton.setTranslationX(300);
        username.setTranslationX(300);
        usernameBox.setTranslationX(300);
        next.setTranslationX(300);
        userIcon.setTranslationX(300);
        special_characters_warning.setTranslationX(300);
        nextButton.setAlpha(v);
        username.setAlpha(v);
        usernameBox.setAlpha(v);
        next.setAlpha(v);
        userIcon.setAlpha(v);
        special_characters_warning.setAlpha(v);
        nextButton.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        username.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        usernameBox.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        next.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        userIcon.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        special_characters_warning.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();


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
        super.onBackPressed();
        // Add popup inflater here for back confirmation
    }
}