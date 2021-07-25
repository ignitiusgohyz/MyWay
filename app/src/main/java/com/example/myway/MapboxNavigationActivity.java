package com.example.myway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

import java.util.Locale;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MapboxNavigationActivity extends AppCompatActivity implements OnNavigationReadyCallback, NavigationListener {

    private NavigationView navigationView;
    private NavigationMapboxMap navigationMapboxMap;
    private MapboxNavigation mapboxNavigation;
    private static DirectionsRoute route;

    private static long parkDuration;
    private Dialog parkingAlarmDialog;
    private TextView display;
    private Button startCountdown;
    private Button cancelCountdown;
    private Button backButtonCountdown;
    private EditText countDownInput;
    private long startTimeInMillis;
    private static CountDownTimer countDownTimer;
    private boolean startButtonClicked;
    private long timeLeftInMillis;
    private long endTime;
    private static boolean delayedAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(),getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_mapboxnavigationactivity);
        navigationView = findViewById(R.id.navigationViewTest);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);
        parkingAlarmDialog = new Dialog(this);
        parkingAlarmDialog.setContentView(R.layout.parking_alarm_dialog);
        display = parkingAlarmDialog.findViewById(R.id.parking_alarm_display);
        startCountdown = parkingAlarmDialog.findViewById(R.id.start_countdown_button);
        cancelCountdown = parkingAlarmDialog.findViewById(R.id.cancel_countdown_button);
        backButtonCountdown = parkingAlarmDialog.findViewById(R.id.back_countdown_button);
        countDownInput = parkingAlarmDialog.findViewById(R.id.countdown_input);
    }

    public static void setParam(DirectionsRoute directionsRoute) {
        route = directionsRoute;
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        if (!isRunning) {
            if (navigationView.retrieveNavigationMapboxMap() != null) {
                this.navigationMapboxMap = navigationView.retrieveNavigationMapboxMap();
                this.mapboxNavigation = navigationView.retrieveMapboxNavigation();
                Log.d("MapboxNavigationActivity Nav>>>>>", "onNavigationReady: " + route.toString());
                NavigationViewOptions options = NavigationViewOptions.builder()
                        .navigationListener(this)
                        .directionsRoute(route)
                        .shouldSimulateRoute(true)
                        .build();
                navigationView.startNavigation(options);
            }
        }
    }

    @Override
    public void onCancelNavigation() {
        navigationView.stopNavigation();
        finish();
    }

    @Override
    public void onNavigationFinished() {
        Toast.makeText(this, "Navigation completed.", Toast.LENGTH_SHORT).show();
        if(delayedAlarm) {
            setAlarm(parkDuration);
            Log.d("FINISH", "ALARM SET");
        }
        delayedAlarm = false;
        finish();
    }

    public boolean hasAlarmSet() {
        return startButtonClicked;
    }

    protected void startTimer() {
        cancelCountdown.setVisibility(View.VISIBLE);
        startCountdown.setVisibility(View.INVISIBLE);
        countDownInput.setVisibility(View.INVISIBLE);
        endTime = System.currentTimeMillis() + timeLeftInMillis;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                startButtonClicked = true;
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                startButtonClicked = false;
                startCountdown.setText("start");
                startCountdown.setVisibility(View.VISIBLE);
                cancelCountdown.setVisibility(View.INVISIBLE);
                countDownInput.setVisibility(View.VISIBLE);
                resetTimer();
                String message = "Parking is almost up!";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MapboxNavigationActivity.this, "myway")
                        .setSmallIcon(R.mipmap.ic_myway_logo)
                        .setContentTitle("MyWay Notification")
                        .setContentText(message)
                        .setPriority(PRIORITY_HIGH)
                        .setAutoCancel(true);


                // if logged out go to loginactivity, else if logged in go to mainactivity
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                String checkbox = preferences.getString("remember","");
                if (checkbox.equals("true")) {
                    Intent intentLoggedIn = new Intent(MapboxNavigationActivity.this, MainActivity.class);
                    intentLoggedIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntentLoggedIn = PendingIntent.getActivity(MapboxNavigationActivity.this, 0, intentLoggedIn, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntentLoggedIn);
                } else {
                    Intent intentLoggedOut = new Intent(MapboxNavigationActivity.this, LoginActivity.class);
                    intentLoggedOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntentLoggedOut = PendingIntent.getActivity(MapboxNavigationActivity.this, 1, intentLoggedOut, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntentLoggedOut);
                }
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
//                notificationManager.notify(1,builder.build());
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1,builder.build());
            }
        }.start();

        startButtonClicked = true;
    }

    protected void resetTimer() {
        startButtonClicked = false;
        startCountdown.setText("start");
        timeLeftInMillis = startTimeInMillis;
        display.setText("Parking Alarm has not been set.");
    }

    protected void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),"Parking Alarm up in %d:%02d:%02d",hours,minutes,seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(), "Parking Alarm up in %02d:%02d",minutes,seconds);
        }
        display.setText(timeLeftFormatted);
    }

    protected void setTime(long millisInput) {
        startTimeInMillis = millisInput;
        resetTimer();
    }

    public void setAlarm(long mInput) {
        setTime(mInput);
        countDownInput.setText("");
        startCountdown.setVisibility(View.INVISIBLE);
        cancelCountdown.setVisibility(View.VISIBLE);
        countDownInput.setVisibility(View.INVISIBLE);
        startTimer();
    }

    @Override
    public void onNavigationRunning() {

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        startTimeInMillis = prefs.getLong("startTimeInMillis", 0);
        timeLeftInMillis = prefs.getLong("timeLeftInMillis", startTimeInMillis);
        startButtonClicked = prefs.getBoolean("startButtonClicked", false);
        if (startButtonClicked) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();
            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                startButtonClicked = false;
                resetTimer();
            } else {
                if (countDownTimer != null) {
                    Log.d("Countdown Timer NULLIFIED >>>>>>>>>>>>>", "TRUE");
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                startTimer();
            }
        }
        navigationView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        startTimeInMillis = prefs.getLong("startTimeInMillis", 0);
        timeLeftInMillis = prefs.getLong("timeLeftInMillis", startTimeInMillis);
        startButtonClicked = prefs.getBoolean("startButtonClicked", false);
        if (startButtonClicked) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();
            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                startButtonClicked = false;
                resetTimer();
            } else {
                if (countDownTimer != null) {
                    Log.d("Countdown Timer NULLIFIED >>>>>>>>>>>>>", "TRUE");
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                startTimer();
            }
        }
        navigationView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putBoolean("startButtonClicked", startButtonClicked);
        editor.putLong("endTime", endTime);
        editor.apply();
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        navigationView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putBoolean("startButtonClicked", startButtonClicked);
        editor.putLong("endTime", endTime);
        editor.apply();
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        navigationView.onPause();
    }

    @Override
    protected void onDestroy() {
        navigationView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!navigationView.onBackPressed()) super.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    public static void setDelayedAlarm(long ms) {
        delayedAlarm = true;
        parkDuration = ms;
    }
}