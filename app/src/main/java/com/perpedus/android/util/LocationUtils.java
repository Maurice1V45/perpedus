package com.perpedus.android.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.perpedus.android.listener.SmartLocationListener;


public class LocationUtils {

    public static final void scanLocation(Context context, String locationProvider, SmartLocationListener locationListener) {

        // retrieve location manager
        LocationManager locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);

        // init min time and distance
        long minTime = 0;
        float minDistance = 0;

        // start scanning for location
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance,
                locationListener, context.getMainLooper());

    }

    public static final Location getLastLocation(Context context) {

        // retrieve location manager
        LocationManager locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
        Location gpsLocation = locationManager.getLastKnownLocation("gps");
        Location networkLocation = locationManager.getLastKnownLocation("network");
        if (gpsLocation == null && networkLocation != null) {
            return networkLocation;
        } else if (networkLocation == null && gpsLocation != null) {
            return gpsLocation;
        } else if (networkLocation.getTime() > gpsLocation.getTime()) {
            return networkLocation;
        } else if (gpsLocation.getTime() > networkLocation.getTime()) {
            return gpsLocation;
        }
        return null;

    }

}
