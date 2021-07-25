package com.example.myway;

// Misc Classes

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationUpdate;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

// TODO
// 1. Set current user location after clicking compass

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, NavigationView.OnNavigationItemSelectedListener {

    // Map Variables
    private MapView mapView;
    private MapboxMap mapboxMap;

    // Location Engine Variables
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;

    // Location Update Variables
    private final MainActivityLocationCallback callback = new MainActivityLocationCallback(this);

    private Button startButton;
    private Button checkParking;
    private double destinationLng;
    private double destinationLat;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private CarmenFeature home;
    private CarmenFeature work;
    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private TextView searchText;
    private String searchText_string;

    private final String ak = "dc82311d-b99a-412e-9f12-6f607b758479";

    private LatLonCoordinate destinationSVY21 = null;

    private DrawerLayout navDrawer;

    private Dialog parkingAlarmDialog;
    private Dialog myProfileDialog;
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
    private ImageButton hamburgerMenu;

    private ImageButton resetNorth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Map access token is configured here
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        // MapView in XML, called after access token is configured.
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        startButton = findViewById(R.id.startNavigation);
        checkParking = findViewById(R.id.checkParking);
        parkingAlarmDialog = new Dialog(this);
        myProfileDialog = new Dialog(this);
        searchText = findViewById(R.id.location_text);
        TextView greetingText = findViewById(R.id.fragment_main_greeting_text);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String greet = "Hello, " + preferences.getString("username", "null");
        greetingText.setText(greet);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        hamburgerMenu = findViewById(R.id.fragment_main_hamburger_menu);
        navDrawer = findViewById(R.id.my_drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ConstraintLayout searchBar = findViewById(R.id.searchBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.nav_open, R.string.nav_close);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        myProfileDialog.setContentView(R.layout.my_account_dialog);
        parkingAlarmDialog.setContentView(R.layout.parking_alarm_dialog);
        display = parkingAlarmDialog.findViewById(R.id.parking_alarm_display);
        startCountdown = parkingAlarmDialog.findViewById(R.id.start_countdown_button);
        cancelCountdown = parkingAlarmDialog.findViewById(R.id.cancel_countdown_button);
        backButtonCountdown = parkingAlarmDialog.findViewById(R.id.back_countdown_button);
        countDownInput = parkingAlarmDialog.findViewById(R.id.countdown_input);

        createNotificationChannel();
        SharedPreferences passEmail = getSharedPreferences("passemail", MODE_PRIVATE);
        String email = passEmail.getString("email", "null");
        View header = ((NavigationView)findViewById((R.id.navigation_view))).getHeaderView(0);
        TextView headerEmail = header.findViewById(R.id.header_email);
        TextView headerUsername = header.findViewById(R.id.header_username);
        headerEmail.setText(email);
        headerUsername.setText(preferences.getString("username", "null"));

        // TODO: extraneous
        ImageButton historyButton = findViewById(R.id.fragment_main_history);
        historyButton.setOnClickListener(v -> Toast.makeText(this, "Feature not implemented yet.", Toast.LENGTH_SHORT).show());
        resetNorth = findViewById(R.id.resetNorth);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ParkingAlarmFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_parking_alarm);
//        }
        hamburgerMenu.setOnClickListener(v -> {
            // If the navigation drawer is not open then open it, if its already open then close it.
            if (!navDrawer.isDrawerOpen(GravityCompat.START)) {
                navDrawer.openDrawer(GravityCompat.START);
            } else {
                navDrawer.closeDrawer(GravityCompat.END);
            }
        });

        resetNorth.setOnClickListener(v -> {
            moveToLastKnown();
        });
    }

    private void moveToLastKnown() {
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(14)
                        .bearing(0)
                        .build()), 4000);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "mywaynotificationchannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("myway", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.navDrawer.isDrawerOpen(GravityCompat.START)) {
            this.navDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (searchText.getText().equals("Where would you like to go?")) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                // this one not too sure yet
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        //            case R.id.nav_parking_alarm:
        //                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ParkingAlarmFragment()).commit();
        //                break;
        if (item.getItemId() == R.id.nav_logout) {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_parking_alarm) {
            parkingAlarmDialog.show();
            startCountdown.setOnClickListener(v -> {
                String input = countDownInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setAlarm(millisInput);
            });
            cancelCountdown.setOnClickListener(v -> {
                cancelCountdown.setVisibility(View.INVISIBLE);
                if (startButtonClicked) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                    resetTimer();
                }
                startCountdown.setVisibility(View.VISIBLE);
                countDownInput.setVisibility(View.VISIBLE);
            });
            backButtonCountdown.setOnClickListener(v -> parkingAlarmDialog.dismiss());
            if (startButtonClicked) {
                updateCountDownText();
            }
        } else if (item.getItemId() == R.id.nav_crowd_sourced_information) {
            Toast.makeText(this, "Feature not implemented yet.", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.nav_settings) {
            Toast.makeText(this, "Feature not implemented yet.", Toast.LENGTH_SHORT).show();
        }


        navDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean hasAlarmSet() {
        return startButtonClicked;
    }

    public void setAlarm(long mInput) {
        setTime(mInput);
        countDownInput.setText("");
        startCountdown.setVisibility(View.INVISIBLE);
        cancelCountdown.setVisibility(View.VISIBLE);
        countDownInput.setVisibility(View.INVISIBLE);
        startTimer();
    }

    protected void setTime(long millisInput) {
        startTimeInMillis = millisInput;
        resetTimer();
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
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "myway")
                        .setSmallIcon(R.mipmap.ic_myway_logo)
                        .setContentTitle("MyWay Notification")
                        .setContentText(message)
                        .setPriority(PRIORITY_HIGH)
                        .setAutoCancel(true);


                // if logged out go to loginactivity, else if logged in go to mainactivity
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                String checkbox = preferences.getString("remember","");
                if (checkbox.equals("true")) {
                    Intent intentLoggedIn = new Intent(MainActivity.this, MainActivity.class);
                    intentLoggedIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntentLoggedIn = PendingIntent.getActivity(MainActivity.this, 0, intentLoggedIn, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntentLoggedIn);
                } else {
                    Intent intentLoggedOut = new Intent(MainActivity.this, LoginActivity.class);
                    intentLoggedOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntentLoggedOut = PendingIntent.getActivity(MainActivity.this, 1, intentLoggedOut, PendingIntent.FLAG_UPDATE_CURRENT);
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

    private void initSearchFab() {
        searchText.setOnClickListener(v -> findViewById(R.id.fab_location_search).performClick());
        findViewById(R.id.fab_location_search).setOnClickListener(view -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .addInjectedFeature(home)
                            .addInjectedFeature(work)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MainActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }

    // Could potentially allow users to add their own location
    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        String symbolIconId = "symbolIconId";
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            searchText.setText(selectedCarmenFeature.text());
            searchText_string = selectedCarmenFeature.text();

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon
            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    LatLng point = new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                            ((Point) selectedCarmenFeature.geometry()).longitude());

                    Log.d("Destination Actual Coordinates?", point.getLatitude() + " AND " + point.getLongitude());

                    destinationSVY21 = new LatLonCoordinate(point.getLatitude(), point.getLongitude());

                    FutureTask<Void> setURADistance = new FutureTask<>(() -> {
                        new GenerateCarparkStatic.generateURA().fillCPDistances(destinationSVY21);
                        return null;
                    });

                    Executor executor = Executors.newFixedThreadPool(1);
                    executor.execute(setURADistance);

                    FutureTask<Void> setHDBDistance = new FutureTask<>(() -> {
                        new GenerateCarparkStatic.generateHDB().fillCPDistances(destinationSVY21);
                        return null;
                    });

                    FutureTask<Void> setLTADistance = new FutureTask<>(() -> {
                        new GenerateCarparkStatic.generateLTA().fillCPDistances(destinationSVY21);
                        return null;
                    });

                    executor.execute(setHDBDistance);
                    executor.execute(setLTADistance);

                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(point)
                                    .zoom(14)
                                    .build()), 4000);
                    onMapClick(point);
                }
            }
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"),
                style -> {
                    initSearchFab();
                    addUserLocations();
                    // Create an empty GeoJSON source using the empty feature collection
                    setUpSource(style);
                    // Set up a new symbol layer for displaying the searched location's feature coordinates
                    setupLayer(style);
                    enableLocationComponent(style);
                    addDestinationIconSymbolLayer(style);

                    Intent intent = getIntent();
                    double longitude = intent.getDoubleExtra("longitude", 0.0);
                    double latitude = intent.getDoubleExtra("latitude", 0.0);
                    String loc = intent.getStringExtra("location");
                    if (loc != null) {
                        searchText.setText(loc);
                        searchText_string = loc;
                    }
                    if (longitude != 0.0)  destinationLng = longitude;
                    if (latitude != 0.0) destinationLat = latitude;

                    if (latitude != 0.0 && longitude != 0.0) {
                        LatLng latLng = new LatLng(latitude, longitude);

                        // NOT SVY21
                        destinationSVY21 = new LatLonCoordinate(latLng.getLatitude(), latLng.getLongitude());

                        FutureTask<Void> setURADistance = new FutureTask<>(() -> {
                            new GenerateCarparkStatic.generateURA().fillCPDistances(destinationSVY21);
                            return null;
                        });

                        Executor executor = Executors.newFixedThreadPool(1);
                        executor.execute(setURADistance);

                        FutureTask<Void> setHDBDistance = new FutureTask<>(() -> {
                            new GenerateCarparkStatic.generateHDB().fillCPDistances(destinationSVY21);
                            return null;
                        });

                        FutureTask<Void> setLTADistance = new FutureTask<>(() -> {
                            new GenerateCarparkStatic.generateLTA().fillCPDistances(destinationSVY21);
                            return null;
                        });

                        executor.execute(setHDBDistance);
                        executor.execute(setLTADistance);

                        onMapClick(latLng);
                    }
                });

        startButton = findViewById(R.id.startNavigation);
        startButton.setOnClickListener((v -> {
            Intent intent = new Intent(MainActivity.this, MapboxNavigationActivity.class);
            Log.d(TAG, "onMapReady: " + currentRoute.toString());
            MapboxNavigationActivity.setParam(currentRoute);
            startActivity(intent);
        }));
        checkParking = findViewById(R.id.checkParking);
        checkParking.setOnClickListener((v -> {
            Intent intent = new Intent(MainActivity.this, Parking.class);
            Bundle bundle = new Bundle();
            bundle.putDouble("destinationLng", destinationLng);
            bundle.putDouble("destinationLat", destinationLat);
            bundle.putString("destination", searchText_string);
            bundle.putString("username", getIntent().getStringExtra("username"));
            String accessToken = generateURAToken.getToken(ak);
            bundle.putString("token", accessToken);
            intent.putExtras(bundle);
            startActivity(intent);
        }));
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // Get instance of component
            locationComponent = mapboxMap.getLocationComponent();

            // Set LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable visibility of component
            locationComponent.setLocationComponentEnabled(true);

            // Set component's camera and render mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.mapbox_marker_icon_default);
        Drawable wrappedDrawable;
        wrappedDrawable = DrawableCompat.wrap(Objects.requireNonNull(unwrappedDrawable));
        DrawableCompat.setTint(Objects.requireNonNull(wrappedDrawable), Color.RED);
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = destinationPoint; // anyhow put first
        if (locationComponent.getLastKnownLocation() != null) {
            originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
        }
        GeoJsonSource source = Objects.requireNonNull(mapboxMap.getStyle()).getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }
        getRoute(originPoint, destinationPoint);
        startButton.setEnabled(true);
        startButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mapboxBlue)));
        checkParking.setEnabled(true);
        checkParking.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mapboxBlue)));
        destinationLat = point.getLatitude();
        destinationLng = point.getLongitude();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(point)
                        .zoom(14)
                        .build()), 4000);
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Objects.requireNonNull(Mapbox.getAccessToken()))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                        Timber.tag(TAG).d("Response Code: %s", response.code());
                        if (response.body() == null) {
                            Timber.tag(TAG).e("No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Timber.tag(TAG).e("No routes found.");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        if (navigationMapRoute != null) {
                            navigationMapRoute.updateRouteVisibilityTo(false);
                            navigationMapRoute.updateRouteArrowVisibilityTo(false);
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable t) {
                        Timber.tag(TAG).e("Error: %s", t.getMessage());
                    }
                });
    }

    // Set up Location Engine and parameters to query device location
    @SuppressWarnings("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L; // 1000L
        long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5; // 5
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    this.recreate();
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permissions Needed")
                            .setMessage("Location Permission is to be enabled for navigation!")
                            .setPositiveButton("Ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION)).create().show();
                } else {
                    // Location denied permanently
                    Snackbar snackbar = Snackbar.make(mapView, "You have previously declined this permission.\n" +
                                    "You must approve this permission in \"Permissions\" in the app settings on your device.",
                            Snackbar.LENGTH_INDEFINITE).setAction("Settings", v -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))));
                    @SuppressWarnings("SpellCheckingInspection")
                    View snackbarView = snackbar.getView();
                    TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setMaxLines(5);
                    snackbar.show();
                }
            }
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static class MainActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        MainActivityLocationCallback(MainActivity mainActivity) {
            this.activityWeakReference = new WeakReference<>(mainActivity);
        }

        // This method runs when device location has changed.
        @SuppressLint("StringFormatInvalid")
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                LocationUpdate.Builder locationUpdateBuilder =  new LocationUpdate.Builder();
                LocationUpdate.Builder locationUpdated = locationUpdateBuilder.location(location);
                LocationUpdate locationUpdate = locationUpdated.build();
                if (location == null) { return; }
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(locationUpdate);
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Timber.tag("LocationChangeActivity").d(exception.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MAIN", "RESUME");
        mapView.onResume();
        if (destinationSVY21 != null) {
            FutureTask<Void> setURADistance = new FutureTask<>(() -> {
                new GenerateCarparkStatic.generateURA().fillCPDistances(destinationSVY21);
                return null;
            });

            Executor executor = Executors.newFixedThreadPool(1);
            executor.execute(setURADistance);

            FutureTask<Void> setHDBDistance = new FutureTask<>(() -> {
                new GenerateCarparkStatic.generateHDB().fillCPDistances(destinationSVY21);
                return null;
            });

            FutureTask<Void> setLTADistance = new FutureTask<>(() -> {
                new GenerateCarparkStatic.generateLTA().fillCPDistances(destinationSVY21);
                return null;
            });

            executor.execute(setHDBDistance);
            executor.execute(setLTADistance);
        }
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
        mapView.onPause();
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
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

}