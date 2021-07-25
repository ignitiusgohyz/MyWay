package com.example.myway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SharedMemory;
import android.util.Log;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

public class MapboxNavigationActivity extends AppCompatActivity implements OnNavigationReadyCallback, NavigationListener {

    private NavigationView navigationView;
    private NavigationMapboxMap navigationMapboxMap;
    private MapboxNavigation mapboxNavigation;
    private static DirectionsRoute route;
    private static boolean delay = false;
    private static long timeDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(),getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_mapboxnavigationactivity);
        navigationView = findViewById(R.id.navigationViewTest);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);
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
        if (delay) {
            SharedPreferences sharedPreferences = getSharedPreferences("delayedAlarm", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("msDelay", timeDelay);
            editor.apply();
        }
        delay = false;
        finish();
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
        navigationView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        delay = true;
        timeDelay = ms;
    }
}