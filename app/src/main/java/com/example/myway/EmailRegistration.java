package com.example.myway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailRegistration extends AppCompatActivity {

    private ImageButton nextButton;
    private EditText email;
    private String email_string;
    private TextView email_warning;
    private boolean canProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_registration);

        nextButton = findViewById(R.id.next_button_email);
        email = findViewById(R.id.register_email);
        email_warning = findViewById(R.id.email_warning);
        canProceed = false;

        nextButton.setOnClickListener(v -> {
            if (canProceed) {
                email_string = email.getText().toString().toLowerCase();
                DatabaseHelper databaseHelper = new DatabaseHelper(EmailRegistration.this);
                if (databaseHelper.emailExists(email_string)) {
                    Toast.makeText(EmailRegistration.this, "Email already in use!", Toast.LENGTH_SHORT).show();
                } else {
                    String username;
                    if (savedInstanceState == null) {
                        Bundle extras = getIntent().getExtras();
                        if (extras == null) {
                            username = null;
                        } else {
                            username = extras.getString("username");
                        }
                    } else {
                        username = (String) savedInstanceState.getSerializable("username");
                    }

                    Intent intent = new Intent(EmailRegistration.this, PasswordRegistration.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email_string);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(EmailRegistration.this, "Please enter a proper email", Toast.LENGTH_SHORT).show();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                canProceed = false;
                if (isValidEmail(email)) {
                    email.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    email_warning.setVisibility(View.INVISIBLE);
                    canProceed = true;
                } else {
                    email.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    email_warning.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static boolean isValidEmail(EditText email) {
        String target = email.getText().toString().trim();
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
        Matcher matcher = emailPattern.matcher(target);
        return matcher.matches();
    }
}