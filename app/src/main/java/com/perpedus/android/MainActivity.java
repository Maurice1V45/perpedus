package com.perpedus.android;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.perpedus.android.comparator.PlacesComparator;
import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.dom.Place;
import com.perpedus.android.dom.PlacesResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.listener.SmartLocationListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.ConverterUtils;
import com.perpedus.android.util.KeyboardUtils;
import com.perpedus.android.util.LocationUtils;
import com.perpedus.android.util.PlacesHelper;
import com.perpedus.android.util.PreferencesUtils;
import com.perpedus.android.util.ScreenUtils;
import com.perpedus.android.util.UrlUtils;
import com.perpedus.android.view.CameraPreview;
import com.perpedus.android.view.PlacesDisplayView;
import com.perpedus.android.view.SearchDrawerLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main Activity. This is where all the magic happens
 */
public class MainActivity extends Activity implements SensorEventListener, MainActivityListener {

    /**
     * Async task that retrieves data about places
     */
    private class RetrievePlacesAsync extends AsyncTask<String, Void, Void> {

        private String name;
        private String radius;
        private float latitude;
        private float longitude;
        private List<String> types;

        @Override
        protected Void doInBackground(String... params) {

            // build the url to the places web service
            String language = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, "en");
            String placesUrl = UrlUtils.buildPlacesLink(name, latitude, longitude, radius, language, types);

            // get the response from the web service
            String placesJson = UrlUtils.getJsonFromUrl(placesUrl);
            PlacesResponse response = new Gson().fromJson(placesJson, PlacesResponse.class);

            if (Constants.GOOGLE_RESPONSE_OK.equals(response.status)) {

                // save the places
                places = ConverterUtils.fromResponseToPlaces(response);

                // sort the places by distance
                Collections.sort(places, new PlacesComparator(myLocation));

            } else {

                // display error toast
                Toast.makeText(MainActivity.this, R.string.google_error, Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            // send the data to the places display view
            placesDisplayView.setPlaces(places);
            placesDisplayView.setClosestAndFurthestLocation();

            // hide the progress view
            hideProgressView();

            // animate focus view
            placeFocusView.setVisibility(View.VISIBLE);
            Animation scaleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_focus_scale);
            placeFocusView.startAnimation(scaleAnimation);

            // display a toast
            Toast.makeText(MainActivity.this, getString(R.string.places_found).replace("%number%", String.valueOf(places.size())), Toast.LENGTH_SHORT).show();
        }

        public void setRadius(String radius) {
            this.radius = radius;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }
    }

    private Camera camera;
    private CameraPreview cameraPreview;
    private SmartLocationListener gpsLocationListener;
    private SmartLocationListener networkLocationListener;
    private FrameLayout screenLayout;
    private PlacesDisplayView placesDisplayView;
    private SensorManager sensorManager;
    private List<Place> places;
    private Location myLocation;
    private boolean firstLocationFound = false;
    private View placeFocusView;
    private View placeDetailsView;
    private TextView placeNameText;
    private TextView placeTypesText;
    private Place focusedPlace;
    private DrawerLayout drawerLayout;
    private View progressView;
    private TextView progressText;
    private View noLandscapeLayout;
    private SearchDrawerLayout drawerContent;
    private boolean portraitOrientation = true;
    private View topBarView;
    private View topSearchButton;
    private View topSettingsButton;
    private boolean topBarVisible = false;
    private boolean topBarAnimationLocked = false;
    private int screenWidth;
    private static final float TOP_BAR_HEIGHT = 48f;
    private static final long TOP_BAR_TIMER_LIMIT = 3000;

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int PERMISSION_REQUEST_LOCATION = 2;

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENT_LOCATION_UPDATED) && intent.hasExtra(Constants.EXTRA_LATITUDE) && intent.hasExtra(Constants.EXTRA_LONGITUDE)) {

                // set provider
                String provider = "";
                if (intent.hasExtra(Constants.EXTRA_PROVIDER)) {
                    provider = intent.getStringExtra(Constants.EXTRA_PROVIDER);
                }
                // set current location
                Location location = new Location(provider);
                location.setLatitude(intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0));
                location.setLongitude(intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0));
                myLocation = location;

                if (placesDisplayView != null) {
                    placesDisplayView.setCurrentLocation(myLocation);
                }

                // if the location is detected for the first time, run a nearby places query
                if (!firstLocationFound) {
                    showProgressView(getString(R.string.retrieve_places));

                    // retrieve nearby places
                    RetrievePlacesAsync async = new RetrievePlacesAsync();
                    async.setLatitude((float) myLocation.getLatitude());
                    async.setLongitude((float) myLocation.getLongitude());
                    async.setRadius("5000");
                    async.setTypes(new ArrayList<String>());
                    async.execute();

                    // unlock right drawer
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    // allow screen tapping
                    setScreenTapListener();
                }
                firstLocationFound = true;
            }
        }
    };

    private CountDownTimer topBarTimer = new CountDownTimer(TOP_BAR_TIMER_LIMIT, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // nothing to do here
        }

        @Override
        public void onFinish() {
            if (topBarVisible) {
                hideTopBar();
            }
        }
    };

    private Animation.AnimationListener visibleAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // nothing to do here
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            placeDetailsView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // nothing to do here
        }
    };

    private Animation.AnimationListener invisibleAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // nothing to do here
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            placeDetailsView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // nothing to do here
        }
    };

    private Animation.AnimationListener topBarVisibleListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // nothing to do here
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            // set new margins
            setTopBarTopMargin(0);

            //update topBarLocked flag
            topBarAnimationLocked = false;

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // nothing to do here
        }
    };

    private Animation.AnimationListener topBarGoneListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // nothing to do here
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            // set new margins
            setTopBarTopMargin(-TOP_BAR_HEIGHT);

            //update topBarLocked flag
            topBarAnimationLocked = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // nothing to do here
        }
    };

    /**
     * Camera preview initializer
     */
    private void initCameraPreview() {

        // Create an instance of Camera
        camera = Camera.open();
        camera.setDisplayOrientation(0);

        cameraPreview = new CameraPreview(this, camera);
        screenLayout.addView(cameraPreview, 0);
    }

    /**
     * Places display view initializer
     */
    private void initPlacesDisplayView() {
        placesDisplayView = new PlacesDisplayView(this, camera.getParameters().getVerticalViewAngle(), camera.getParameters().getHorizontalViewAngle(), MainActivity.this);
        placesDisplayView.setCurrentLocation(myLocation);
        screenLayout.addView(placesDisplayView, 1);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nothing to do here
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // initialize android device sensor capabilities
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // init location listener
        registerReceiver(locationReceiver, new IntentFilter(Constants.INTENT_LOCATION_UPDATED));

        // check if user has the required hardware in order to run this app
        checkUserHasHardwareRequirements();

        // keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // init places list
        places = new ArrayList<Place>();

        // init location listeners
        gpsLocationListener = new SmartLocationListener(MainActivity.this, "gps");
        networkLocationListener = new SmartLocationListener(MainActivity.this, "network");

        // init views and listeners
        initViews();
        initListeners();
        setTopBarTopMargin(-TOP_BAR_HEIGHT);

        // check for required permissions
        checkPermissions();


    }


    @Override
    protected void onStop() {

        // unregister receivers
        unregisterReceivers();
        super.onStop();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // register sensor listener
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        // register location receiver
        registerReceiver(locationReceiver, new IntentFilter(Constants.INTENT_LOCATION_UPDATED));

        // scan for new location
        LocationUtils.scanLocation(MainActivity.this, "gps", gpsLocationListener);
        LocationUtils.scanLocation(MainActivity.this, "network", networkLocationListener);

        // Create an instance of Camera
        if (camera == null) {
            camera = Camera.open();
            camera.setDisplayOrientation(0);
            cameraPreview = new CameraPreview(this, camera);
            screenLayout.addView(cameraPreview, 0);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

//        Log.w("asd",
//                Math.round(event.values[0]) + "     " + Math.round(event.values[1]) + "     "
//                        + Math.round(event.values[2]));
        if (placesDisplayView != null) {
            float coordinateX = event.values[0];
            float coordinateY = event.values[1];
            float coordinateZ = event.values[2];

            placesDisplayView.setSensorX(coordinateX);
            placesDisplayView.setSensorY(coordinateY);
            placesDisplayView.setSensorZ(coordinateZ);

            placesDisplayView.invalidate();

            checkLandscape(coordinateZ);
        }

    }

    /**
     * Displays a "Landscape not available" view
     *
     * @param coordinateZ
     */
    private void checkLandscape(float coordinateZ) {
        if (Math.abs(coordinateZ) >= 45 && portraitOrientation) {

            // landscape
            portraitOrientation = false;
            placesDisplayView.setVisibility(View.GONE);
            placeFocusView.setVisibility(View.GONE);
            noLandscapeLayout.setVisibility(View.VISIBLE);
            noLandscapeLayout.setRotation(coordinateZ > 0 ? 90 : -90);
            handleFocusedPlace(null);
        } else if (Math.abs(coordinateZ) < 45 && !portraitOrientation) {

            //portrait
            portraitOrientation = true;
            placesDisplayView.setVisibility(View.VISIBLE);
            placeFocusView.setVisibility(View.VISIBLE);
            noLandscapeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Views initializer
     */
    private void initViews() {
        screenLayout = (FrameLayout) findViewById(R.id.camera_preview);
        placeFocusView = findViewById(R.id.place_focus_view);
        placeDetailsView = findViewById(R.id.place_details_view);
        placeNameText = (TextView) findViewById(R.id.place_name_text);
        placeTypesText = (TextView) findViewById(R.id.place_types_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerContent = (SearchDrawerLayout) findViewById(R.id.right_drawer);
        progressView = findViewById(R.id.progress_layout);
        progressText = (TextView) findViewById(R.id.progress_text);
        noLandscapeLayout = findViewById(R.id.no_landscape_layout);
        topBarView = findViewById(R.id.top_bar_view);
        topSearchButton = findViewById(R.id.top_search_button);
        topSettingsButton = findViewById(R.id.top_settings_button);

    }

    /**
     * Listeners initializer
     */
    private void initListeners() {
        drawerContent.setMainActivityListener(MainActivity.this);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // slowly hide other views
                placeFocusView.setAlpha(1f - slideOffset);
                placesDisplayView.setAlpha(1f - slideOffset);
                placeDetailsView.setAlpha(1f - slideOffset);

                // hide top bar if visible
                if (topBarVisible) {
                    hideTopBar();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // nothing to do here
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // nothing to do here
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // nothing to do here
            }
        });
        topSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // hide top bar
                hideTopBar();

                // open search drawer
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        topSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        placeDetailsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (focusedPlace != null) {

                    // hide views
                    placesDisplayView.setAlpha(0f);
                    placeFocusView.setAlpha(0f);
                    placeDetailsView.setAlpha(0f);

                    // show place details dialog
                    DialogUtils.showPlaceDetailsDialog(getFragmentManager(), MainActivity.this, focusedPlace.getPlaceId(), screenWidth);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        // close drawer if opened
        if (drawerLayout.isDrawerVisible(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPlacesDisplayViewCreated(int width, int height) {

        screenWidth = width;

        // set focused view size
        ViewGroup.LayoutParams layoutParams = placeFocusView.getLayoutParams();
        layoutParams.width = width / 2;
        layoutParams.height = width / 2;
    }

    @Override
    public void onFocusedPlace(Place focusedPlace) {
        handleFocusedPlace(focusedPlace);
    }

    private void handleFocusedPlace(Place focusedPlace) {
        if (focusedPlace == null) {

            // there is currently no focused place
            if (this.focusedPlace != null) {

                // assign new focused place and animate place details
                this.focusedPlace = null;
                Animation invisibleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_place_details_invisible);
                invisibleAnimation.setAnimationListener(invisibleAnimationListener);
                placeDetailsView.startAnimation(invisibleAnimation);
            }
        } else {

            // there is a already focused place
            if (this.focusedPlace == null) {

                // animate place details
                Animation visibleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_place_details_visible);
                visibleAnimation.setAnimationListener(visibleAnimationListener);
                placeDetailsView.startAnimation(visibleAnimation);

                // set details view data
                setDetailsViewData(focusedPlace);
            } else {

                // set details view data
                setDetailsViewData(focusedPlace);
            }

            // assign new focused place
            this.focusedPlace = focusedPlace;
        }
    }

    /**
     * Sets the title and types of the focused place
     *
     * @param focusedPlace
     */
    private void setDetailsViewData(Place focusedPlace) {

        // set place title
        placeNameText.setText(focusedPlace.getName());

        // set place types
        String placeTypesString = " - ";
        for (String placeType : focusedPlace.getTypes()) {
            placeTypesString += PlacesHelper.getInstance().getPlaceTypeName(placeType) + " - ";
        }
        placeTypesText.setText(placeTypesString);
    }

    @Override
    public void onSearchButtonPressed(String name, String radius, List<String> types) {

        // close drawer and hide keyboard
        KeyboardUtils.closeKeyboard(MainActivity.this);
        drawerLayout.closeDrawer(Gravity.RIGHT);

        // show progress view
        showProgressView(getString(R.string.retrieve_places));

        // clear current places
        places.clear();

        // hide focus view
        placeFocusView.setVisibility(View.GONE);

        // call for places query
        RetrievePlacesAsync async = new RetrievePlacesAsync();
        async.setLatitude((float) myLocation.getLatitude());
        async.setLongitude((float) myLocation.getLongitude());
        async.setTypes(types);
        async.setName(name);
        async.setRadius(radius);
        async.execute();
    }

    @Override
    public void onPlaceTypesUpdated(List<String> selectedTypes) {

        // set new selected types and update selected types list
        drawerContent.setSelectedTypes(selectedTypes);
        drawerContent.updateSelectedTypes();
    }

    @Override
    public void onSearchLanguageSelected(String language) {

        // store new language into shared prefs
        PreferencesUtils.storePreference(Constants.PREF_SELECTED_SEARCH_LANGUAGE, language);

        // update search language button
        drawerContent.updateSearchLanguageButton();
    }

    @Override
    public void onPlaceDetailsDialogDismiss() {

        // show views
        placesDisplayView.setAlpha(1f);
        placeFocusView.setAlpha(1f);
        placeDetailsView.setAlpha(1f);
    }

    /**
     * Checks if user has required hardware features
     */
    private void checkUserHasHardwareRequirements() {
        PackageManager packageManager = getPackageManager();

        // check if user has camera
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(MainActivity.this, R.string.fatal_error_no_camera, Toast.LENGTH_LONG).show();
            unregisterReceivers();
            finish();
        }

        // check if user has orientation sensor
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (orientationSensor == null) {
            Toast.makeText(MainActivity.this, R.string.fatal_error_no_orientation, Toast.LENGTH_LONG).show();
            unregisterReceivers();
            finish();
        }

        // check if user has gps
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            Toast.makeText(MainActivity.this, R.string.fatal_error_no_gps, Toast.LENGTH_LONG).show();
            unregisterReceivers();
            finish();
        }
    }

    /**
     * Unregisters receivers
     */
    private void unregisterReceivers() {

        // unregister sensor listener
        sensorManager.unregisterListener(this);

        // unregister location receiver
        try {
            unregisterReceiver(locationReceiver);
        } catch (Exception e) {
            // receiver was not registered
        }

        // remove location updates
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // remove location updates on the listener
            LocationManager locationManager = (LocationManager) getSystemService(
                    Context.LOCATION_SERVICE);
            locationManager.removeUpdates(gpsLocationListener);
            locationManager.removeUpdates(networkLocationListener);
        }

        if (camera != null) {

            // release camera resources
            camera.stopPreview();
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.release();
            camera = null;
            screenLayout.removeView(cameraPreview);
        }
    }

    /**
     * Checks for permissions and if granted continues initializing views
     */
    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            return;
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        // init camera preview
        initCameraPreview();

        // init places display view
        initPlacesDisplayView();

        // scan for new location
        LocationUtils.scanLocation(MainActivity.this, "gps", gpsLocationListener);
        LocationUtils.scanLocation(MainActivity.this, "network", networkLocationListener);

        // display progress view
        showProgressView(getString(R.string.scan_location));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {

                // If request is cancelled, the result arrays are empty. 
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted
                    checkPermissions();

                } else {

                    // permission denied
                    Toast.makeText(MainActivity.this, R.string.permission_denied_camera, Toast.LENGTH_LONG).show();
                    unregisterReceivers();
                    finish();
                }
                return;
            }
            case PERMISSION_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted
                    checkPermissions();

                } else {

                    // permission denied
                    Toast.makeText(MainActivity.this, R.string.permission_denied_location, Toast.LENGTH_LONG).show();
                    unregisterReceivers();
                    finish();
                }
                return;
            }
            default:
                break;
        }
    }

    /**
     * Displays the progress view
     *
     * @param text
     */
    private void showProgressView(String text) {
        progressView.setVisibility(View.VISIBLE);
        progressText.setText(text);
    }

    /**
     * Hides the progress view
     */
    private void hideProgressView() {
        progressView.setVisibility(View.GONE);
    }

    private void setScreenTapListener() {

        // enables tapping on the screen
        placesDisplayView.setClickable(true);
        placesDisplayView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (topBarVisible) {

                    // hide top bar animation
                    hideTopBar();
                } else {

                    // display top bar animation
                    displayTopBar();
                }
            }
        });
    }

    private void displayTopBar() {

        if (!topBarAnimationLocked) {

            // update flag
            topBarVisible = true;

            // play animation
            Animation topBarVisibleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_top_bar_visible);
            topBarVisibleAnimation.setAnimationListener(topBarVisibleListener);
            topBarView.startAnimation(topBarVisibleAnimation);

            //update topBarLocked flag
            topBarAnimationLocked = true;

            // start a timer that will hide the top bar if visible for too long
            topBarTimer.start();
        }
    }

    private void hideTopBar() {

        if (!topBarAnimationLocked) {

            // update flag
            topBarVisible = false;

            // play animation
            Animation topBarGoneAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_top_bar_gone);
            topBarGoneAnimation.setAnimationListener(topBarGoneListener);
            topBarView.startAnimation(topBarGoneAnimation);

            //update topBarLocked flag
            topBarAnimationLocked = true;

            // cancel the currently running timer
            topBarTimer.cancel();
        }
    }

    private void setTopBarTopMargin(float topMarginPx) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) ScreenUtils.fromDpToPixels(MainActivity.this, TOP_BAR_HEIGHT));
        layoutParams.setMargins(0, (int) ScreenUtils.fromDpToPixels(MainActivity.this, topMarginPx), 0, 0);
        topBarView.setLayoutParams(layoutParams);
    }

}
