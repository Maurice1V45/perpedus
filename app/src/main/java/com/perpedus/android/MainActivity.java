package com.perpedus.android;


import android.Manifest;
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
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.perpedus.android.comparator.PlacesComparator;
import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.dom.Place;
import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.dom.PlacesResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.listener.RetrievePlaceDetailsListener;
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
import com.perpedus.android.view.RoundedCornersTransformation;
import com.perpedus.android.view.SearchDrawerLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main Activity. This is where all the magic happens
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, MainActivityListener, RetrievePlaceDetailsListener {

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
            String placesUrl = UrlUtils.buildPlacesLink(name, latitude, longitude, radius, language, null);

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
    private TextView progressText;
    private View noLandscapeLayout;
    private SearchDrawerLayout drawerContent;
    private boolean portraitOrientation = true;
    private int screenWidth;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

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
        placesDisplayView.setPlaces(places);
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

            PlacesResponse response = new Gson().fromJson(getIntent().getStringExtra(Constants.EXTRA_PLACES), PlacesResponse.class);
            if (Constants.GOOGLE_RESPONSE_OK.equals(response.status)) {

                // save the places
                places = ConverterUtils.fromResponseToPlaces(response);

                // sort the places by distance
                Collections.sort(places, new PlacesComparator(myLocation));

            } else {

                // display error toast
                Toast.makeText(MainActivity.this, R.string.google_error, Toast.LENGTH_SHORT).show();
            }
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

        // animate focus view
        placeFocusView.setVisibility(View.VISIBLE);
        Animation scaleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_focus_scale);
        placeFocusView.startAnimation(scaleAnimation);

    }

    /**
     * Toolbar initializer
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
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
        placeAddressText = (TextView) findViewById(R.id.place_address_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerContent = (SearchDrawerLayout) findViewById(R.id.right_drawer);
        progressView = findViewById(R.id.progress_layout);
        progressText = (TextView) findViewById(R.id.progress_text);
        noLandscapeLayout = findViewById(R.id.no_landscape_layout);
        placeImage = (ImageView) findViewById(R.id.place_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
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
            PlacesHelper.getInstance().retrievePlaceDetailsAsync(MainActivity.this, focusedPlace.getPlaceId(), language);

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
    public void onSearchButtonPressed(String name, String radius, List<String> types) {

        // close drawer and hide keyboard
        KeyboardUtils.closeKeyboard(MainActivity.this);
        drawerLayout.closeDrawer(Gravity.LEFT);

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

    @Override
    public void onPlaceDetailsRetrieved(String response) {

        final PlaceDetailsResponse detailsResponse = new Gson().fromJson(response, PlaceDetailsResponse.class);
        for (Place place : places) {
            if (place.getPlaceId().equals(detailsResponse.result.placeId)) {

                // store place details
                place.setDetails(detailsResponse);

                if (focusedPlace != null) {

                    // update focused place image
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (detailsResponse.result.photos == null || detailsResponse.result.photos.length == 0) {

                                // load no photo
                                placeImage.setImageResource(R.drawable.icon_no_photo);

                            } else {

                                // load photo
                                String reference = detailsResponse.result.photos[0].reference;
                                Picasso
                                        .with(MainActivity.this)
                                        .load(UrlUtils.buildPlacePhotoLink(reference, (int) ScreenUtils.fromDpToPixels(MainActivity.this, 84f)))
                                        .placeholder(R.drawable.progress)
                                        .transform(new RoundedCornersTransformation(12, 0))
                                        .error(R.drawable.icon_no_photo)
                                        .into(placeImage);
                            }
                        }
                    });
                }
                break;
            }
        }
    }

    @Override
    public void onPlaceDetailsRetrievedError() {

    }

}
