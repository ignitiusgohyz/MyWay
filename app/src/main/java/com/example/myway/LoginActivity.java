package com.example.myway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

    private ImageButton visibilityButton;
    private EditText createdUsername;
    private String createdUsername_string;
    private EditText createdPassword;
    private String createdPassword_string;
    private float v = 0;
    private final String key = "dc82311d-b99a-412e-9f12-6f607b758479";
    private String rememberedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageButton loginButton = findViewById(R.id.login);
        createdUsername = findViewById(R.id.username);
        createdPassword = findViewById(R.id.password);
        ImageView usernameAndPassword = findViewById(R.id.username_password);
        CheckBox rememberMe = findViewById(R.id.rememberMe);
        ImageButton registerButton = findViewById(R.id.register);
        Button tempClearDatabaseButton = findViewById(R.id.tempclearbutton);
        visibilityButton = findViewById(R.id.visibility_button);
        DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);

        // Does these task in the background of Login
        FutureTask<Void> setURA = new FutureTask<>(() -> {
            InputStream uraParking = getResources().openRawResource(R.raw.uraparking);
            GenerateCarparkStatic URA = new GenerateCarparkStatic.generateURA();
            URA.setList(URA.readCSV(uraParking));
            Log.d("URA>>>>", "DONE");
            return null;
        });

        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(setURA);

        FutureTask<Void> setHDB = new FutureTask<>(() -> {
            InputStream hdbParking = getResources().openRawResource(R.raw.hdbparking);
            GenerateCarparkStatic HDB = new GenerateCarparkStatic.generateHDB();
            HDB.setList(HDB.readCSV(hdbParking));

            Log.d("HDB>>>>", "DONE");
            return null;
        });

        FutureTask<Void> setLTA = new FutureTask<>(() -> {
            InputStream ltaParking = getResources().openRawResource(R.raw.ltaparking);
            GenerateCarparkStatic LTA = new GenerateCarparkStatic.generateLTA();
            LTA.setList(LTA.readCSV(ltaParking));
            Log.d("LTA>>>>", "DONE");
            return null;
        });

        executor.execute(setHDB);
        executor.execute(setLTA);


        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);

        loginButton.setOnClickListener(v -> {
            createdUsername_string = createdUsername.getText().toString().toLowerCase().trim();
            rememberedUsername = createdUsername_string;
            createdPassword_string = createdPassword.getText().toString().trim();

            if (createdUsername_string.length() == 0 || createdPassword_string.length() == 0) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.usernameExists(createdUsername_string)) {
                boolean authenticity = databaseHelper.verifyPassword(createdUsername_string, createdPassword_string);
                if (authenticity) {
                    loadingDialog.startLoading();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        loadingDialog.dismissDialog();
                        Toast.makeText(LoginActivity.this, "Logging In...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", createdUsername_string);
                        startActivity(intent);
                    }, 1000);

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

        if (checkbox.equals("true")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Log.d("LoginActivity", "rememberedUsername: " + rememberedUsername);
            intent.putExtra("username", rememberedUsername);
            startActivity(intent);
        }

        tempClearDatabaseButton.setOnClickListener(v -> {
                databaseHelper.clearDatabase();
                Toast.makeText(LoginActivity.this, "All records deleted", Toast.LENGTH_SHORT).show();
                });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, UsernameRegistration.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm").setMessage("Are you sure?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            super.onBackPressed();
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}