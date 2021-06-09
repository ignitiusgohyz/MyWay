package com.example.myway;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordRegistration extends AppCompatActivity {

    private EditText first_password;
    private EditText second_password;
    private TextView password_warning;
    private ImageButton passwordInfoButton;
    private ImageButton visibilityButton;
    private CheckBox userAgreementCheckBox;
    private boolean canProceedPassword = false;
    private boolean canProceedChecked = false;
    private ImageButton registerButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_registration);

        first_password = findViewById(R.id.register_password_top);
        second_password = findViewById(R.id.register_password_bottom);
        password_warning = findViewById(R.id.registration_password_warning);
        passwordInfoButton = findViewById(R.id.register_password_requirement_popup);
        visibilityButton = findViewById(R.id.registration_password_visibility_toggle);
        userAgreementCheckBox = findViewById(R.id.user_agreement_checkbox);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
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
                startActivity(intent);

            } else if (canProceedPassword) {
                Toast.makeText(PasswordRegistration.this, "Please tick the checkbox", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PasswordRegistration.this, "Please enter a proper password", Toast.LENGTH_SHORT).show();
            }
        });

        userAgreementCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> canProceedChecked = buttonView.isChecked());

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

        passwordInfoButton.setOnClickListener(v -> {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup_password_requirement, null);

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
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
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    password_warning.setText(R.string.different_password_warning);
                    password_warning.setVisibility(View.VISIBLE);
                } else if (isInvalidPassword(first_password)) {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    password_warning.setText(R.string.password_requirement);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
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
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    password_warning.setText(R.string.different_password_warning);
                    password_warning.setVisibility(View.VISIBLE);
                } else if (isInvalidPassword(first_password)) {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    password_warning.setText(R.string.password_requirement);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
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
        super.onBackPressed();
        // TODO
    }
}