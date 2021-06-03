package com.example.myway;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO
// 1. Replace all deprecated methods --> Suppressed them, is this fine to do so?
// 2. Reposition Toast messages to somewhere more visible such as the top of the UI.
// 3. Add in number in registration fields.
// 4. Ensure e-mail is truly in e-mail format.
// 5. Disallow special characters for username as well.
// 6. Lookout for areas to improve code efficiency to prevent frame skips.
// 7. Potentially make the password info button permanently visible beside visibility button.
// 8. Clear login page on resume.

@SuppressWarnings("deprecation")
public class RegisterActivity extends AppCompatActivity {

    private ImageButton visibilityButton, passwordInfoButton;
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
    private TextView invalid_email_warning;

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.register_button);
        username = findViewById(R.id.register_username);
        first_password = findViewById(R.id.first_password);
        second_password = findViewById(R.id.second_password);
        visibilityButton = findViewById(R.id.visibility_button);
        passwordInfoButton = findViewById(R.id.password_requirement_button);
        different_password_1 = findViewById(R.id.different_password_warning);
        different_password_2 = findViewById(R.id.different_password_warning2);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        special_characters_warning = findViewById(R.id.special_character_warning);
        invalid_email_warning = findViewById(R.id.invalid_email);

        first_name.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        last_name.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        username.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        email.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

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
                    first_name.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else {
                    first_name.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
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
                    last_name.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    special_characters_warning.setVisibility(View.VISIBLE);
                } else {
                    last_name.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
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
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setText(R.string.different_password_warning);
                    different_password_2.setText(R.string.different_password_warning);
                    different_password_1.setVisibility(View.VISIBLE);
                    different_password_2.setVisibility(View.VISIBLE);
                } else if (isInvalidPassword(first_password)) {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setText(R.string.password_requirement);
                    different_password_2.setText(R.string.password_requirement);
                    different_password_1.setVisibility(View.VISIBLE);
                    different_password_2.setVisibility(View.VISIBLE);
                    passwordInfoButton.setVisibility(View.VISIBLE);
                } else {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setVisibility(View.INVISIBLE);
                    different_password_2.setVisibility(View.INVISIBLE);
                    passwordInfoButton.setVisibility(View.INVISIBLE);
                }
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
                if (isValidEmail(email)) {
                email.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                invalid_email_warning.setVisibility(View.INVISIBLE);
                } else {
                    email.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    invalid_email_warning.setVisibility(View.VISIBLE);
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
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setText(R.string.different_password_warning);
                    different_password_2.setText(R.string.different_password_warning);
                    different_password_1.setVisibility(View.VISIBLE);
                    different_password_2.setVisibility(View.VISIBLE);
                } else if (isInvalidPassword(first_password)) {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setText(R.string.password_requirement);
                    different_password_2.setText(R.string.password_requirement);
                    different_password_1.setVisibility(View.VISIBLE);
                    different_password_2.setVisibility(View.VISIBLE);
                    passwordInfoButton.setVisibility(View.VISIBLE);
                } else {
                    first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    second_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    different_password_1.setVisibility(View.INVISIBLE);
                    different_password_2.setVisibility(View.INVISIBLE);
                    passwordInfoButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        registerButton.setOnClickListener(v -> {
            username_string = username.getText().toString().toLowerCase().trim();
            first_password_string = first_password.getText().toString().trim();
            second_password_string = second_password.getText().toString().trim();
            first_name_string = first_name.getText().toString().trim();
            last_name_string = last_name.getText().toString().trim();
            email_string = email.getText().toString().trim();

            UserModel userModel;
            DatabaseHelper databaseHelper = new DatabaseHelper(RegisterActivity.this);

            if (isEmpty(username_string) || isEmpty(first_password_string) || isEmpty(second_password_string) ||
                isEmpty(first_name_string) || isEmpty(last_name_string) || isEmpty(email_string)) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                if (databaseHelper.usernameExists(username_string)) {
                    Toast.makeText(RegisterActivity.this, "Username already in use!", Toast.LENGTH_SHORT).show();
                } else if (databaseHelper.emailExists(email_string)) {
                    Toast.makeText(RegisterActivity.this, "Email already in use!", Toast.LENGTH_SHORT).show();
                } else {
                    userModel = new UserModel(-1, first_name_string, last_name_string, email_string, username_string, first_password_string);
                    databaseHelper.addOne(userModel);
                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
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
        finish();
    }

    public static boolean isValidEmail(EditText email) {
        String target = email.getText().toString().trim();
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
        Matcher matcher = emailPattern.matcher(target);
        return matcher.matches();
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
}
