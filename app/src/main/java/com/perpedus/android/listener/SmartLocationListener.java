package com.perpedus.android.listener;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.perpedus.android.util.Constants;

/**
 * Location listener that sends broadcasts when the location was updated
 */
public class SmartLocationListener implements LocationListener {

    private Context context;
    private String provider;

    /**
     * Constructor
     *
     * @param context
     * @param provider
     */
    public SmartLocationListener(Context context, String provider) {
        this.context = context;
        this.provider = provider;
    }

    public void onLocationChanged(Location location) {

        if (location != null) {
            Log.w("asd", provider + " location detected: " + location.getLatitude() + "," + location.getLongitude());

            // send broadcast that location was updated
            Intent locationUpdateIntent = new Intent(Constants.INTENT_LOCATION_UPDATED);
            locationUpdateIntent.putExtra(Constants.EXTRA_LATITUDE, location.getLatitude());
            locationUpdateIntent.putExtra(Constants.EXTRA_LONGITUDE, location.getLongitude());
            context.sendBroadcast(locationUpdateIntent);
        }

    }

    public void onProviderDisabled(String provider) {
        // nothing to do here
    }

    public void onProviderEnabled(String provider) {
        // nothing to do here
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // nothing to do here
    }

}