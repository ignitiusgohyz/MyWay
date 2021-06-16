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

public class EmailRegistration extends AppCompatActivity {

    private EditText email;
    private TextView email_warning;
    private String email_string;
    private boolean canProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_registration);

        ImageView registrationBox = findViewById(R.id.registration_email_box);
        email = findViewById(R.id.register_email);
        ImageButton nextButton = findViewById(R.id.next_button_email);
        TextView nextText = findViewById(R.id.next_email);
        ImageView emailIcon = findViewById(R.id.email_icon);
        email_warning = findViewById(R.id.email_warning);
        canProceed = false;

        registrationBox.setTranslationX(300);
        email.setTranslationX(300);
        nextButton.setTranslationX(300);
        nextText.setTranslationX(300);
        emailIcon.setTranslationX(300);
        email_warning.setTranslationX(300);

        float v1 = 0;
        registrationBox.setAlpha(v1);
        email.setAlpha(v1);
        nextButton.setAlpha(v1);
        nextText.setAlpha(v1);
        emailIcon.setAlpha(v1);
        email_warning.setAlpha(v1);

        registrationBox.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        email.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        nextButton.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        nextText.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        emailIcon.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        email_warning.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();

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
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                    email.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    email_warning.setVisibility(View.INVISIBLE);
                    canProceed = true;
                } else {
                    email.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
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