package com.perpedus.android.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

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

    /**
     * Returns the location in front of the user with 50 meters
     *
     * @param myLocation
     * @param sensorX
     * @return
     */
    public static Location getFrontLocation(Location myLocation, float sensorX) {
        double bearing = Math.toRadians(sensorX);
        double distance = 0.05d / 6371d;
        double myLatitude = Math.toRadians(myLocation.getLatitude());
        double myLongitude = Math.toRadians(myLocation.getLongitude());
        double frontLocationLat = Math.asin(Math.sin(myLatitude) * Math.cos(distance) + Math.cos(myLatitude) * Math.sin(distance) * Math.cos(bearing));
        double frontLocationLong = myLongitude + Math.atan2(Math.sin(bearing) * Math.sin(distance) * Math.cos(myLatitude), Math.cos(distance) - Math.sin(myLatitude) * Math.sin(frontLocationLat));
        Location frontLocation = new Location(myLocation.getProvider());
        frontLocation.setLatitude(Math.toDegrees(frontLocationLat));
        frontLocation.setLongitude(Math.toDegrees(frontLocationLong));
        return frontLocation;
    }

}
