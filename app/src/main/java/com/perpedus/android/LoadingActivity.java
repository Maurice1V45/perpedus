package com.perpedus.android;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.perpedus.android.listener.RetrievePlacesListener;
import com.perpedus.android.listener.SmartLocationListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.LocationUtils;
import com.perpedus.android.util.PlacesHelper;
import com.perpedus.android.util.PreferencesUtils;

public class LoadingActivity extends Activity implements RetrievePlacesListener {

    private LocationManager locationManager;
    private SmartLocationListener gpsLocationListener;
    private SmartLocationListener networkLocationListener;

    private String provider;
    private double latitude;
    private double longitude;

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int PERMISSION_REQUEST_LOCATION = 2;

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENT_LOCATION_UPDATED) && intent.hasExtra(Constants.EXTRA_LATITUDE) && intent.hasExtra(Constants.EXTRA_LONGITUDE)) {

                // set provider
                if (intent.hasExtra(Constants.EXTRA_PROVIDER)) {
                    provider = intent.getStringExtra(Constants.EXTRA_PROVIDER);
                }
                // set latitude and longitude
                latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);

                // continue with places retrieving
                onFirstLocationReceived();

                // unregister receivers and listeners
                unregisterLocationListener();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        // check if user has the required hardware in order to run this app
        checkUserHasHardwareRequirements();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // check for required permissions
        checkPermissions();
    }

    private void onFirstLocationReceived() {

        // get language
        String language = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, Constants.DEFAULT_LANGUAGE);

        // retrieve places
        PlacesHelper.getInstance().retrievePlacesAsync(LoadingActivity.this, null, "5000", (float) latitude, (float) longitude, language, null);
    }

    @Override
    protected void onStop() {
        unregisterLocationListener();
        super.onStop();
    }

    private void unregisterLocationListener() {

        // unregister location receiver
        try {
            unregisterReceiver(locationReceiver);
        } catch (Exception e) {
            // receiver was not registered
        }

        // remove location updates
        if (ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // remove location updates on the listener
            locationManager.removeUpdates(gpsLocationListener);
            locationManager.removeUpdates(networkLocationListener);
        }
    }

    /**
     * Checks if user has required hardware features
     */
    private void checkUserHasHardwareRequirements() {
        PackageManager packageManager = getPackageManager();

        // check if user has camera
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(LoadingActivity.this, R.string.fatal_error_no_camera, Toast.LENGTH_LONG).show();
            unregisterLocationListener();
            finish();
        }

        // check if user has orientation sensor
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (orientationSensor == null) {
            Toast.makeText(LoadingActivity.this, R.string.fatal_error_no_orientation, Toast.LENGTH_LONG).show();
            unregisterLocationListener();
            finish();
        }

        // check if user has gps
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            Toast.makeText(LoadingActivity.this, R.string.fatal_error_no_gps, Toast.LENGTH_LONG).show();
            unregisterLocationListener();
            finish();
        }
    }

    /**
     * Checks for permissions and if granted continues initializing views
     */
    private void checkPermissions() {

        // request permissions if needed
        if (ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            return;
        } else if (ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        // scan for current location
        initAndScanLocation();
    }

    // retrieves currentLocation
    private void initAndScanLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // init location listener
        registerReceiver(locationReceiver, new IntentFilter(Constants.INTENT_LOCATION_UPDATED));

        // init location listeners
        gpsLocationListener = new SmartLocationListener(LoadingActivity.this, LocationManager.GPS_PROVIDER);
        networkLocationListener = new SmartLocationListener(LoadingActivity.this, LocationManager.NETWORK_PROVIDER);

        // scan for new location
        LocationUtils.scanLocation(LoadingActivity.this, LocationManager.GPS_PROVIDER, gpsLocationListener);
        LocationUtils.scanLocation(LoadingActivity.this, LocationManager.NETWORK_PROVIDER, networkLocationListener);
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
                    Toast.makeText(LoadingActivity.this, R.string.permission_denied_camera, Toast.LENGTH_LONG).show();
                    unregisterLocationListener();
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
                    Toast.makeText(LoadingActivity.this, R.string.permission_denied_location, Toast.LENGTH_LONG).show();
                    unregisterLocationListener();
                    finish();
                }
                return;
            }
            default:
                break;
        }
    }

    @Override
    public void onPlacesRetrieved(String response) {

        // launch main activity
        Intent mainActivityIntent = new Intent(LoadingActivity.this, MainActivity.class);
        mainActivityIntent.putExtra(Constants.EXTRA_PROVIDER, provider);
        mainActivityIntent.putExtra(Constants.EXTRA_LATITUDE, latitude);
        mainActivityIntent.putExtra(Constants.EXTRA_LONGITUDE, longitude);
        mainActivityIntent.putExtra(Constants.EXTRA_PLACES, response);
        startActivity(mainActivityIntent);
        finish();
    }

    @Override
    public void onPlacesRetrievedError() {

    }
}
