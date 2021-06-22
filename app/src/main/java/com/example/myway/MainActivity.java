    package com.example.myway;

// Misc Classes

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.BitmapFactory;
    import android.graphics.Color;
    import android.graphics.drawable.Drawable;
    import android.location.Location;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Handler;
    import android.provider.Settings;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.content.res.AppCompatResources;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.drawable.DrawableCompat;

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
    import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
    import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
    import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
    import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

    import org.jetbrains.annotations.NotNull;
    import org.json.JSONObject;

    import java.io.IOException;
    import java.lang.ref.WeakReference;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;
    import java.util.concurrent.Callable;
    import java.util.concurrent.CompletableFuture;
    import java.util.concurrent.Executor;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.concurrent.FutureTask;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import timber.log.Timber;

    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

// TODO
// 1. Set current user location after clicking compass

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

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

    private TextView greetingText;

    private Button URAbutton;
    private final String ak = "dc82311d-b99a-412e-9f12-6f607b758479";

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
        searchText = findViewById(R.id.location_text);
        greetingText = findViewById(R.id.fragment_main_greeting_text);
        URAbutton = findViewById(R.id.URAtest);
        String username = "Hello, " + getIntent().getStringExtra("username");
        greetingText.setText(username);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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

                    SVY21Coordinate destinationSVY21 = new LatLonCoordinate(point.getLatitude(), point.getLongitude()).asSVY21();

                    FutureTask<Void> setURADistance = new FutureTask<>(() -> {
                        generateURADetails.fillCPDistances(destinationSVY21);
                        return null;
                    });

                    Executor executor = Executors.newFixedThreadPool(1);
                    executor.execute(setURADistance);

                    FutureTask<Void> setHDBDistance = new FutureTask<>(() -> {
                        generateHDBDetails.fillCPDistances(destinationSVY21);
                        return null;
                    });

                    executor.execute(setHDBDistance);

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
        this.mapboxMap.addOnMapClickListener(MainActivity.this);
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
                });
        startButton = findViewById(R.id.startNavigation);
        startButton.setOnClickListener((v -> {
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                    .directionsRoute(currentRoute)
                    .shouldSimulateRoute(true)
                    .build();
            NavigationLauncher.startNavigation(MainActivity.this, options);
        }));
        checkParking = findViewById(R.id.checkParking);
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        checkParking.setOnClickListener((v -> {

            Intent intent = new Intent(MainActivity.this, Parking.class);
            Bundle bundle = new Bundle();
            bundle.putDouble("destinationLng", destinationLng);
            bundle.putDouble("destinationLat", destinationLat);
            bundle.putString("destination", searchText_string);
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
        startButton.setBackgroundResource(R.color.mapboxBlue);
        checkParking.setEnabled(true);
        checkParking.setBackgroundResource(R.color.mapboxBlue);
        destinationLat = point.getLatitude();
        destinationLng = point.getLongitude();
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
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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