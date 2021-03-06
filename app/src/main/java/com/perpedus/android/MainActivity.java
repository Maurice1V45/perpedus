package com.perpedus.android;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.perpedus.android.comparator.PlacesComparator;
import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.dom.Place;
import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.dom.PlacesResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.listener.SmartLocationListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.ConverterUtils;
import com.perpedus.android.util.CoordinateProvider;
import com.perpedus.android.util.CoordinateUtils;
import com.perpedus.android.util.KeyboardUtils;
import com.perpedus.android.util.LocationUtils;
import com.perpedus.android.util.PreferencesUtils;
import com.perpedus.android.util.ScreenUtils;
import com.perpedus.android.util.UrlUtils;
import com.perpedus.android.view.CameraPreview;
import com.perpedus.android.view.CustomToast;
import com.perpedus.android.view.PlacesDisplayView;
import com.perpedus.android.view.RoundedCornersTransformation;
import com.perpedus.android.view.SearchDrawerLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Main Activity. This is where all the magic happens
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, MainActivityListener {

    private Camera camera;
    private CameraPreview cameraPreview;
    private SmartLocationListener gpsLocationListener;
    private SmartLocationListener networkLocationListener;
    private FrameLayout screenLayout;
    private PlacesDisplayView placesDisplayView;
    private SensorManager sensorManager;
    private List<Place> places = new ArrayList<Place>();
    private Location myLocation;
    private View placeFocusView;
    private View placeDetailsView;
    private TextView placeNameText;
    private TextView placeAddressText;
    private ImageView placeImage;
    private Place focusedPlace;
    private DrawerLayout drawerLayout;
    private View progressView;
    private View noLandscapeLayout;
    private View noLandscapeInnerLayout;
    private SearchDrawerLayout drawerContent;
    private boolean portraitOrientation = true;
    private int screenWidth;
    private Toolbar toolbar;
    private boolean viewsAlphaLocked;
    private View settingsButton;
    private boolean sensorCalibrationDialogVisible = false;
    private long lastGpsLocationTime = 0;
    private boolean backPressedOnce;
    private boolean noGps;
    private boolean noGpsViewProhibited;
    private View noGpsView;
    private View loadingIcon1;
    private View loadingIcon2;
    private View loadingIcon3;
    private View loadingIcon4;
    private View loadingLayout;
    private View tutorialLayout;
    private TextView tutorialText;
    private TextView tutorialCompletedText;
    private TextView tutorialNextButton;
    private int tutorialLevel = 0;
    private View scanFrontButton;
    private static final int BACK_TO_EXIT_INTERVAL = 2000;

    private class PlacesCallback implements Callback<PlacesResponse> {

        private boolean onlyClosest;

        public PlacesCallback(boolean onlyClosest) {
            this.onlyClosest = onlyClosest;
        }

        @Override
        public void success(PlacesResponse placesResponse, Response response) {

            if (Constants.GOOGLE_RESPONSE_OK.equals(placesResponse.status)) {

                // save the places
                places = ConverterUtils.fromResponseToPlaces(placesResponse);

                // sort the places by distance
                Collections.sort(places, new PlacesComparator(myLocation));

                // remove other places if onlyClosest flag is true
                if (onlyClosest && !places.isEmpty()) {
                    places.subList(0, places.size() - 1).clear();
                }

                // send the data to the places display view
                placesDisplayView.setPlaces(places);
                placesDisplayView.setClosestAndFurthestLocation();

                // display a toast
                Toast.makeText(MainActivity.this, getString(places.size() == 1 ? R.string.toast_place_found : R.string.toast_places_found).replace("%number%", String.valueOf(places.size())), Toast.LENGTH_SHORT).show();

            } else if (Constants.GOOGLE_RESPONSE_NO_RESULTS.equals(placesResponse.status)) {

                // clear the places
                places.clear();

                // send the data to the places display view
                placesDisplayView.setPlaces(places);
                placesDisplayView.setClosestAndFurthestLocation();

                // display a toast
                Toast.makeText(MainActivity.this, getString(R.string.toast_no_places_found), Toast.LENGTH_SHORT).show();

            } else {

                // display error toast
                Toast.makeText(MainActivity.this, R.string.toast_places_error, Toast.LENGTH_SHORT).show();
            }

            // hide the progress view
            hideProgressView();

            // animate focus view
            animateFocusView();
        }

        @Override
        public void failure(RetrofitError error) {

            // display error toast
            Toast.makeText(MainActivity.this, R.string.toast_places_error, Toast.LENGTH_SHORT).show();

            // hide the progress view
            hideProgressView();

            // animate focus view
            animateFocusView();
        }
    }

    private class ScanFrontCallback implements Callback<PlacesResponse> {

        private float targetBearing;

        public ScanFrontCallback(float targetBearing) {
            this.targetBearing = targetBearing;
        }

        @Override
        public void success(PlacesResponse placesResponse, Response response) {

            if (Constants.GOOGLE_RESPONSE_OK.equals(placesResponse.status)) {

                List<Place> foundPlaces = ConverterUtils.fromResponseToPlaces(placesResponse);

                if (!foundPlaces.isEmpty()) {
                    float bestBearingDifference = 360f;
                    Place bestPlace = null;

                    // find the closest place to the direction the user is looking
                    for (Place place : foundPlaces) {
                        float placeBearing = myLocation.bearingTo(place.getLocation());
                        float bearingDifference = Math.abs(CoordinateUtils.getAngleForDegrees(placeBearing, targetBearing));
                        if (bearingDifference < bestBearingDifference) {
                            bestBearingDifference = bearingDifference;
                            bestPlace = place;
                        }
                    }

                    // save the place
                    places = new ArrayList<Place>();
                    places.add(bestPlace);
                }

                // send the data to the places display view
                placesDisplayView.setPlaces(places);
                placesDisplayView.setClosestAndFurthestLocation();

                // display a toast
                Toast.makeText(MainActivity.this, getString(places.size() == 1 ? R.string.toast_place_found : R.string.toast_places_found).replace("%number%", String.valueOf(places.size())), Toast.LENGTH_SHORT).show();

            } else if (Constants.GOOGLE_RESPONSE_NO_RESULTS.equals(placesResponse.status)) {

                // clear the places
                places.clear();

                // send the data to the places display view
                placesDisplayView.setPlaces(places);
                placesDisplayView.setClosestAndFurthestLocation();

                // display a toast
                Toast.makeText(MainActivity.this, getString(R.string.toast_no_places_found), Toast.LENGTH_SHORT).show();

            } else {

                // display error toast
                Toast.makeText(MainActivity.this, R.string.toast_places_error, Toast.LENGTH_SHORT).show();
            }

            // hide the progress view
            hideProgressView();

            // animate focus view
            animateFocusView();
        }

        @Override
        public void failure(RetrofitError error) {

            // display error toast
            Toast.makeText(MainActivity.this, R.string.toast_places_error, Toast.LENGTH_SHORT).show();

            // hide the progress view
            hideProgressView();

            // animate focus view
            animateFocusView();
        }
    }

    private Callback<PlaceDetailsResponse> placeDetailsCallback = new Callback<PlaceDetailsResponse>() {

        @Override
        public void success(PlaceDetailsResponse placeDetailsResponse, Response response) {

            if (Constants.GOOGLE_RESPONSE_OK.equals(placeDetailsResponse.status)) {

                if (placeDetailsResponse.result.reviews != null) {

                    // remove empty reviews
                    List<PlaceDetailsResponse.Review> reviewList = new ArrayList<PlaceDetailsResponse.Review>();
                    for (PlaceDetailsResponse.Review review : placeDetailsResponse.result.reviews) {
                        if (review.comment != null && !review.comment.isEmpty()) {
                            reviewList.add(review);
                        }
                    }
                    placeDetailsResponse.result.reviews = reviewList.toArray(new PlaceDetailsResponse.Review[reviewList.size()]);
                }

                for (Place place : places) {
                    if (place.getPlaceId().equals(placeDetailsResponse.result.placeId)) {

                        // store place details
                        place.setDetails(placeDetailsResponse);

                        if (focusedPlace != null) {

                            if (placeDetailsResponse.result.photos == null || placeDetailsResponse.result.photos.length == 0) {

                                // load no photo
                                placeImage.setImageResource(R.drawable.icon_no_photo);

                            } else {

                                // load photo
                                String reference = placeDetailsResponse.result.photos[0].reference;
                                Picasso
                                        .with(MainActivity.this)
                                        .load(UrlUtils.buildPlacePhotoLink(reference, (int) ScreenUtils.fromDpToPixels(MainActivity.this, 84f)))
                                        .placeholder(R.drawable.progress)
                                        .transform(new RoundedCornersTransformation(12, 0))
                                        .error(R.drawable.icon_no_photo)
                                        .into(placeImage);
                            }
                        }
                        break;
                    }
                }

                // send broadcast that place details have been received
                Intent intent = new Intent(Constants.INTENT_PLACE_DETAILS_RECEIVED);
                intent.putExtra(Constants.EXTRA_PLACE_DETAILS, placeDetailsResponse);
                sendBroadcast(intent);
            }

        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENT_LOCATION_UPDATED) && intent.hasExtra(Constants.EXTRA_PROVIDER) && intent.hasExtra(Constants.EXTRA_LATITUDE) && intent.hasExtra(Constants.EXTRA_LONGITUDE)) {

                // get provider
                String provider = intent.getStringExtra(Constants.EXTRA_PROVIDER);

                // if we received a network location and a gps location was received recently, ignore
                // the network location, else save the location
                if (!(LocationManager.NETWORK_PROVIDER.equals(provider) && System.currentTimeMillis() - lastGpsLocationTime < 10000)) {

                    // set current location
                    Location location = new Location(provider);
                    location.setLatitude(intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0));
                    location.setLongitude(intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0));
                    myLocation = location;

                    if (placesDisplayView != null) {
                        placesDisplayView.setCurrentLocation(myLocation);
                    }

                    // if its network provider, show no gps view
                    if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
                        noGps = true;
                        if (!noGpsViewProhibited) {
                            noGpsView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                // if location is gps provided, save its time
                if (LocationManager.GPS_PROVIDER.equals(provider)) {
                    lastGpsLocationTime = System.currentTimeMillis();

                    // hide no gps view
                    noGps = false;
                    noGpsView.setVisibility(View.GONE);
                }
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

    /**
     * Camera preview initializer
     */
    private void initCameraPreview() {

        // Create an instance of Camera
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        setCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, camera);

        cameraPreview = new CameraPreview(this, camera);
        screenLayout.addView(cameraPreview, 0);
    }

    private void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * Places display view initializer
     */
    private void initPlacesDisplayView() {
        placesDisplayView = new PlacesDisplayView(this, camera.getParameters().getVerticalViewAngle(), camera.getParameters().getHorizontalViewAngle(), MainActivity.this, places);
        placesDisplayView.setAlpha(0f);
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

        // get extras
        if (getIntent().hasExtra(Constants.EXTRA_PROVIDER)) {
            myLocation = new Location(getIntent().getStringExtra(Constants.EXTRA_PROVIDER));
        }
        if (getIntent().hasExtra(Constants.EXTRA_LATITUDE)) {
            myLocation.setLatitude(getIntent().getDoubleExtra(Constants.EXTRA_LATITUDE, 0));
        }
        if (getIntent().hasExtra(Constants.EXTRA_LONGITUDE)) {
            myLocation.setLongitude(getIntent().getDoubleExtra(Constants.EXTRA_LONGITUDE, 0));
        }
        if (getIntent().hasExtra(Constants.EXTRA_PLACES)) {

            PlacesResponse response = (PlacesResponse) getIntent().getSerializableExtra(Constants.EXTRA_PLACES);

            // save the places
            places = ConverterUtils.fromResponseToPlaces(response);

            // sort the places by distance
            Collections.sort(places, new PlacesComparator(myLocation));

        }

        // initialize android device sensor capabilities
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // init views and listeners
        initViews();
        initListeners();
        initToolbar();

        // init camera preview
        initCameraPreview();

        // init places display view
        initPlacesDisplayView();

        // set no gps to true of gps was not detected
        if (LocationManager.NETWORK_PROVIDER.equals(myLocation.getProvider())) {
            noGps = true;
        }

        // display sensors calibration dialog
        displaySensorsCalibrationDialog();

    }

    /**
     * Toolbar initializer
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.main_drawer_open, R.string.main_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                if (!viewsAlphaLocked) {

                    // slowly hide other views
                    placeFocusView.setAlpha(1f - slideOffset);
                    placesDisplayView.setAlpha(1f - slideOffset);
                    placeDetailsView.setAlpha(1f - slideOffset);
                    noGpsView.setAlpha(1f - slideOffset);
                }

            }
        };
        drawerLayout.setDrawerListener(toggle);
        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
        toggle.syncState();
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

        // init location listeners
        gpsLocationListener = new SmartLocationListener(MainActivity.this, LocationManager.GPS_PROVIDER);
        networkLocationListener = new SmartLocationListener(MainActivity.this, LocationManager.NETWORK_PROVIDER);

        // scan for new location
        LocationUtils.scanLocation(MainActivity.this, LocationManager.GPS_PROVIDER, gpsLocationListener);
        LocationUtils.scanLocation(MainActivity.this, LocationManager.NETWORK_PROVIDER, networkLocationListener);

        // Create an instance of Camera
        if (camera == null) {
            initCameraPreview();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (placesDisplayView != null && !sensorCalibrationDialogVisible) {
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
            tutorialLayout.setVisibility(View.GONE);
            noGpsViewProhibited = true;
            noGpsView.setVisibility(View.GONE);
            noLandscapeLayout.setVisibility(View.VISIBLE);
            noLandscapeInnerLayout.setRotation(coordinateZ > 0 ? 90 : -90);
            handleFocusedPlace(null);
        } else if (Math.abs(coordinateZ) < 45 && !portraitOrientation) {

            //portrait
            portraitOrientation = true;
            placesDisplayView.setVisibility(View.VISIBLE);
            placeFocusView.setVisibility(View.VISIBLE);
            noGpsViewProhibited = false;
            if (noGps) {
                noGpsView.setVisibility(View.VISIBLE);
            }
            noLandscapeLayout.setVisibility(View.GONE);
            if (tutorialLevel > 0) {
                tutorialLayout.setVisibility(View.VISIBLE);
            }
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
        placeAddressText = (TextView) findViewById(R.id.place_address_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerContent = (SearchDrawerLayout) findViewById(R.id.right_drawer);
        progressView = findViewById(R.id.progress_layout);
        noLandscapeLayout = findViewById(R.id.no_landscape_layout);
        noLandscapeInnerLayout = findViewById(R.id.no_landscape_inner_layout);
        placeImage = (ImageView) findViewById(R.id.place_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        settingsButton = findViewById(R.id.settings_button);
        loadingIcon1 = findViewById(R.id.loading_icon1);
        loadingIcon2 = findViewById(R.id.loading_icon2);
        loadingIcon3 = findViewById(R.id.loading_icon3);
        loadingIcon4 = findViewById(R.id.loading_icon4);
        loadingLayout = findViewById(R.id.loading_layout);
        tutorialLayout = findViewById(R.id.tutorial_layout);
        tutorialText = (TextView) findViewById(R.id.tutorial_text);
        tutorialCompletedText = (TextView) findViewById(R.id.tutorial_completed_text);
        tutorialNextButton = (TextView) findViewById(R.id.tutorial_next_button);
        noGpsView = findViewById(R.id.no_gps_view);
        scanFrontButton = findViewById(R.id.scan_front_button);
    }

    /**
     * Listeners initializer
     */
    private void initListeners() {
        drawerContent.setMainActivityListener(MainActivity.this);
        placeDetailsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (focusedPlace != null) {

                    // enable next button if tutorial level 2 is on
                    if (tutorialLevel == 2) {
                        tutorialNextButton.setVisibility(View.VISIBLE);
                        tutorialCompletedText.setVisibility(View.VISIBLE);
                    }

                    // hide views
                    placesDisplayView.setAlpha(0f);
                    placeFocusView.setAlpha(0f);
                    placeDetailsView.setAlpha(0f);
                    noGpsView.setAlpha(0f);

                    // show place details dialog
                    DialogUtils.showPlaceDetailsDialog(getFragmentManager(), MainActivity.this, focusedPlace.getDetails(), screenWidth);
                }
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        tutorialNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementTutorialLevel();
            }
        });
        scanFrontButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Location frontLocation = LocationUtils.getFrontLocation(myLocation, placesDisplayView.getSensorX());

                // show progress view
                showProgressView();

                // clear current places
                places.clear();

                // hide focus view
                placeFocusView.setVisibility(View.GONE);

                String language = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, Constants.DEFAULT_LANGUAGE);
                PerpedusApplication.getInstance().getPlacesService().getPlaces(frontLocation.getLatitude() + "," + frontLocation.getLongitude(), "50", language, null, null, Constants.PLACES_KEY, new ScanFrontCallback(myLocation.bearingTo(frontLocation)));
            }
        });
    }

    @Override
    public void onBackPressed() {

        // close drawer if opened
        if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            if (backPressedOnce) {

                // exit the app
                super.onBackPressed();
            } else {

                // display back to exit toast
                backPressedOnce = true;
                CustomToast.makeText(this, getString(R.string.toast_back_to_exit), Toast.LENGTH_SHORT).show();

                // reset flag after the interval time
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backPressedOnce = false;
                    }
                }, BACK_TO_EXIT_INTERVAL);
            }
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

    /**
     * Makes focus view visible and animates it
     */
    private void animateFocusView() {
        placeFocusView.setVisibility(View.VISIBLE);
        Animation scaleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_focus_scale);
        placeFocusView.startAnimation(scaleAnimation);
    }

    @Override
    public void onFocusedPlace(Place focusedPlace) {
        handleFocusedPlace(focusedPlace);
    }

    private void handleFocusedPlace(Place focusedPlace) {
        if (focusedPlace == null) {

            // there is currently no focused place

            // show no gps view if the case
            if (noGps && !noGpsViewProhibited) {
                noGpsView.setVisibility(View.VISIBLE);
            }

            if (this.focusedPlace != null) {

                // assign new focused place and animate place details
                this.focusedPlace = null;
                Animation invisibleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_place_details_invisible);
                invisibleAnimation.setAnimationListener(invisibleAnimationListener);
                placeDetailsView.startAnimation(invisibleAnimation);
            }
        } else {

            // hide no gps view if the case
            if (noGps) {
                noGpsView.setVisibility(View.GONE);
            }

            // enable next button if tutorial level 1 is on
            if (tutorialLevel == 1) {
                tutorialNextButton.setVisibility(View.VISIBLE);
                tutorialCompletedText.setVisibility(View.VISIBLE);
            }

            // there is a already focused place
            if (this.focusedPlace == null) {

                // animate place details
                Animation visibleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_place_details_visible);
                visibleAnimation.setAnimationListener(visibleAnimationListener);
                placeDetailsView.startAnimation(visibleAnimation);

                // set details view data
                setDetailsViewData(focusedPlace);

            } else if (!this.focusedPlace.getPlaceId().equals(focusedPlace.getPlaceId())) {

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

        // set place address
        placeAddressText.setText(focusedPlace.getAddress());

        // check for place details
        if (focusedPlace.getDetails() == null) {

            // no details, attempt to retrieve them
            String language = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, Constants.DEFAULT_LANGUAGE);
            PerpedusApplication.getInstance().getPlacesService().getPlaceDetails(focusedPlace.getPlaceId(), language, Constants.PLACES_KEY, placeDetailsCallback);

            // set image as loading
            placeImage.setImageResource(R.drawable.progress);

        } else {

            // place details exist
            if (focusedPlace.getDetails().result.photos == null || focusedPlace.getDetails().result.photos.length == 0) {

                // place has no photos, display no photos icon
                placeImage.setImageResource(R.drawable.icon_no_photo);
            } else {

                // load first photo
                String reference = focusedPlace.getDetails().result.photos[0].reference;
                if (reference != null && !reference.isEmpty()) {
                    Picasso
                            .with(MainActivity.this)
                            .load(UrlUtils.buildPlacePhotoLink(reference, (int) ScreenUtils.fromDpToPixels(MainActivity.this, 84f)))
                            .placeholder(R.drawable.progress)
                            .transform(new RoundedCornersTransformation(12, 0))
                            .error(R.drawable.icon_no_photo)
                            .into(placeImage);
                }
            }
        }

    }

    @Override
    public void onSearchButtonPressed(String name, String radius, String type, boolean onlyClosest) {

        // enable next button if tutorial level 3 is on
        if (tutorialLevel == 3) {
            tutorialNextButton.setVisibility(View.VISIBLE);
            tutorialCompletedText.setVisibility(View.VISIBLE);
        }

        // close drawer and hide keyboard
        KeyboardUtils.closeKeyboard(MainActivity.this);
        drawerLayout.closeDrawer(Gravity.LEFT);

        // show progress view
        showProgressView();

        // clear current places
        places.clear();

        // hide focus view
        placeFocusView.setVisibility(View.GONE);

        String language = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, Constants.DEFAULT_LANGUAGE);
        PerpedusApplication.getInstance().getPlacesService().getPlaces(myLocation.getLatitude() + "," + myLocation.getLongitude(), radius, language, name, type, Constants.PLACES_KEY, new PlacesCallback(onlyClosest));

    }

    @Override
    public void onPlaceTypeUpdated(String selectedType) {

        // set new selected type and update it
        drawerContent.setSelectedType(selectedType);
        drawerContent.updateSelectedType();
    }

    @Override
    public void onPlaceDetailsDialogDismiss() {

        // show views
        placesDisplayView.setAlpha(1f);
        placeFocusView.setAlpha(1f);
        placeDetailsView.setAlpha(1f);
        noGpsView.setAlpha(1f);
    }

    @Override
    public void onPlaceTypesDialogOpen() {

        // lock views alpha
        viewsAlphaLocked = true;

        // close navigation drawer
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onPlaceTypesDialogDismiss() {

        // unlock views alpha
        viewsAlphaLocked = false;

        // close navigation drawer
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onSensorsCalibrationDialogDismiss() {
        sensorCalibrationDialogVisible = false;
        animateFocusView();
        checkTutorialCompleted();
        // show views
        placesDisplayView.setAlpha(1f);
        placeDetailsView.setAlpha(1f);
        noGpsView.setAlpha(1f);
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
     * Displays the progress view
     */
    private void showProgressView() {
        progressView.setVisibility(View.VISIBLE);
        playProgressAnimation();
    }

    /**
     * Hides the progress view
     */
    private void hideProgressView() {
        progressView.setVisibility(View.GONE);
    }

    /**
     * Displays sensors calibration dialog
     */
    private void displaySensorsCalibrationDialog() {
        sensorCalibrationDialogVisible = true;
        DialogUtils.showCalibrateSensorsDialog(getFragmentManager(), MainActivity.this);
    }

    private void playProgressAnimation() {
        Animation loadingAnimation1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_loading1);
        Animation loadingAnimation2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_loading2);
        Animation loadingAnimation3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_loading3);
        Animation loadingAnimation4 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_loading4);
        Animation rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_rotate);
        loadingIcon1.startAnimation(loadingAnimation1);
        loadingIcon2.startAnimation(loadingAnimation2);
        loadingIcon3.startAnimation(loadingAnimation3);
        loadingIcon4.startAnimation(loadingAnimation4);
        loadingLayout.startAnimation(rotate);
    }

    private void incrementTutorialLevel() {
        tutorialLevel++;
        tutorialCompletedText.setVisibility(View.GONE);
        switch (tutorialLevel) {
            case 1:
                tutorialText.setText(R.string.tutorial_text_level_1);
                tutorialNextButton.setVisibility(View.GONE);
                break;
            case 2:
                tutorialText.setText(R.string.tutorial_text_level_2);
                tutorialNextButton.setVisibility(View.GONE);
                break;
            case 3:
                tutorialText.setText(R.string.tutorial_text_level_3);
                tutorialNextButton.setVisibility(View.GONE);
                break;
            case 4:
                tutorialText.setText(R.string.tutorial_text_level_4);
                tutorialNextButton.setText(R.string.tutorial_button_finish);
                tutorialNextButton.setVisibility(View.VISIBLE);
                break;
            case 5:
                tutorialLevel = 0;
                tutorialLayout.setVisibility(View.GONE);
                PreferencesUtils.storePreference(Constants.PREF_TUTORIAL_COMPLETED, true);
                break;
            default:
                break;
        }
    }

    private void checkTutorialCompleted() {
        if (!PreferencesUtils.getPreferences().getBoolean(Constants.PREF_TUTORIAL_COMPLETED, false)) {

            // init tutorial
            tutorialLayout.setVisibility(View.VISIBLE);
            incrementTutorialLevel();
        }
    }

}
