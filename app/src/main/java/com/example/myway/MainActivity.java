    package com.example.myway;

// Misc Classes

    import android.annotation.SuppressLint;
    import android.graphics.BitmapFactory;
    import android.graphics.Color;
    import android.graphics.drawable.Drawable;
    import android.location.Location;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.content.res.AppCompatResources;
    import androidx.core.graphics.drawable.DrawableCompat;

    import com.mapbox.android.core.location.LocationEngine;
    import com.mapbox.android.core.location.LocationEngineCallback;
    import com.mapbox.android.core.location.LocationEngineProvider;
    import com.mapbox.android.core.location.LocationEngineRequest;
    import com.mapbox.android.core.location.LocationEngineResult;
    import com.mapbox.android.core.permissions.PermissionsListener;
    import com.mapbox.android.core.permissions.PermissionsManager;
    import com.mapbox.api.directions.v5.models.DirectionsResponse;
    import com.mapbox.api.directions.v5.models.DirectionsRoute;
    import com.mapbox.geojson.Feature;
    import com.mapbox.geojson.Point;
    import com.mapbox.mapboxsdk.Mapbox;
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
    import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
    import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
    import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
    import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
    import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
    import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

    import org.jetbrains.annotations.NotNull;

    import java.lang.ref.WeakReference;
    import java.util.List;
    import java.util.Objects;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import timber.log.Timber;

    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
    import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

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
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Map access token is configured here
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // MapView in XML, called after access token is configured.
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        startButton = findViewById(R.id.startNavigation);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        startButton.setOnClickListener(v -> {

        });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.addOnMapClickListener(MainActivity.this);
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"),
                style -> {
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

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // If permission is granted
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
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
            // Permission Variables
            PermissionsManager permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
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
        this.recreate();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
        //  enableLocationComponent(mapboxMap.getStyle());
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