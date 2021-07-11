package com.example.myway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordRegistration extends AppCompatActivity {

    private EditText first_password;
    private ImageButton visibilityButton;
    private EditText second_password;
    private TextView password_warning;
    private boolean canProceedPassword = false;
    private boolean canProceedChecked = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_registration);

        ImageView firstPasswordBox = findViewById(R.id.register_password_top_box);
        first_password = findViewById(R.id.register_password_top);
        visibilityButton = findViewById(R.id.registration_password_visibility_toggle);
        ImageView secondPasswordBox = findViewById(R.id.register_password_bottom_box);
        second_password = findViewById(R.id.register_password_bottom);
        ImageButton passwordRequirement = findViewById(R.id.register_password_requirement_popup);
        ImageButton finishButton = findViewById(R.id.register_button);
        TextView finishText = findViewById(R.id.textView);
        TextView userAgreements = findViewById(R.id.user_agreement_1);
        CheckBox checkBox = findViewById(R.id.user_agreement_checkbox);
        TextView userAgreements2 = findViewById(R.id.user_agreement);
        TextView privacyPolicy = findViewById(R.id.privacy_policy);
        TextView and = findViewById(R.id.and);
        password_warning = findViewById(R.id.registration_password_warning);

        firstPasswordBox.setTranslationX(300);
        first_password.setTranslationX(300);
        visibilityButton.setTranslationX(300);
        secondPasswordBox.setTranslationX(300);
        second_password.setTranslationX(300);
        passwordRequirement.setTranslationX(300);
        finishButton.setTranslationX(300);
        finishText.setTranslationX(300);
        userAgreements.setTranslationX(300);
        checkBox.setTranslationX(300);
        userAgreements2.setTranslationX(300);
        privacyPolicy.setTranslationX(300);
        and.setTranslationX(300);
        password_warning.setTranslationX(300);

        float v2 = 0;
        firstPasswordBox.setAlpha(v2);
        first_password.setAlpha(v2);
        visibilityButton.setAlpha(v2);
        secondPasswordBox.setAlpha(v2);
        second_password.setAlpha(v2);
        passwordRequirement.setAlpha(v2);
        finishButton.setAlpha(v2);
        finishText.setAlpha(v2);
        userAgreements.setAlpha(v2);
        checkBox.setAlpha(v2);
        userAgreements2.setAlpha(v2);
        privacyPolicy.setAlpha(v2);
        and.setAlpha(v2);
        password_warning.setAlpha(v2);

        firstPasswordBox.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        first_password.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        visibilityButton.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        secondPasswordBox.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        second_password.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        passwordRequirement.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        finishButton.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        finishText.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        userAgreements.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        checkBox.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        userAgreements2.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        privacyPolicy.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        and.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();
        password_warning.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();


        finishButton.setOnClickListener(v -> {
            if (canProceedPassword && canProceedChecked) {
                String username;
                String email;
                String password = first_password.getText().toString();
                if (savedInstanceState == null) {
                    Bundle extras = getIntent().getExtras();
                    if (extras == null) {
                        username = null;
                        email = null;
                    } else {
                        username = extras.getString("username");
                        email = extras.getString("email");
                    }
                } else {
                    username = (String) savedInstanceState.getSerializable("username");
                    email = (String) savedInstanceState.getSerializable("email");

                }

                UserModel userModel;
                DatabaseHelper databaseHelper = new DatabaseHelper(PasswordRegistration.this);

                userModel = new UserModel(-1,"", "", email, username, password);
                databaseHelper.addOne(userModel);
                Toast.makeText(PasswordRegistration.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PasswordRegistration.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (canProceedPassword) {
                Toast.makeText(PasswordRegistration.this, "Please tick the checkbox", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PasswordRegistration.this, "Please enter a proper password", Toast.LENGTH_SHORT).show();
            }
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> canProceedChecked = buttonView.isChecked());

        visibilityButton.setOnClickListener(v -> {
            if (visibilityButton.getContentDescription().equals("Password Hidden")) {
                first_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                second_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                visibilityButton.setContentDescription("Password Visible");
                visibilityButton.setImageResource(R.drawable.ic_password_visible);
            } else {
                first_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                second_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                visibilityButton.setContentDescription("Password Hidden");
                visibilityButton.setImageResource(R.drawable.ic_password_not_visible);
            }
            first_password.setSelection(first_password.getText().length());
            second_password.setSelection(second_password.getText().length());
        });

        passwordRequirement.setOnClickListener(v -> {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams")
            View popupView = layoutInflater.inflate(R.layout.popup_password_requirement, null);

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL, 0, 0);
            popupView.setOnTouchListener((v1, event) -> {
                popupWindow.dismiss();
                return true;
            });
        });

        first_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                canProceedPassword = false;
                if (!(first_password.getText().toString().equals(second_password.getText().toString()))) {
                    first_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    second_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    password_warning.setText(R.string.different_password_warning);
                    password_warning.setVisibility(View.VISIBLE);
                } else if (isInvalidPassword(first_password)) {
                    first_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    second_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    password_warning.setText(R.string.password_requirement);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    first_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    second_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    password_warning.setVisibility(View.INVISIBLE);
                    canProceedPassword = true;
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
                canProceedPassword = false;
                if (!(first_password.getText().toString().equals(second_password.getText().toString()))) {
                    first_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    second_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    password_warning.setText(R.string.different_password_warning);
                    password_warning.setVisibility(View.VISIBLE);
                } else if (isInvalidPassword(first_password)) {
                    first_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    second_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    password_warning.setText(R.string.password_requirement);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    first_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    second_password.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    password_warning.setVisibility(View.INVISIBLE);
                    canProceedPassword = true;
                }
            }
        });
    }


    private boolean isInvalidPassword(EditText pw) {
        String temp_pw = pw.getText().toString().trim();
        Pattern pattern = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[!@#$%^*&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$");
        Matcher matcher = pattern.matcher(temp_pw);
        return !matcher.matches();
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