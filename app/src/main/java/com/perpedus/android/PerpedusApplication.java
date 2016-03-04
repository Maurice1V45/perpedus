package com.perpedus.android;

import android.app.Application;

import com.perpedus.android.api.PlacesService;
import com.perpedus.android.util.Constants;

import retrofit.RestAdapter;

/**
 * Application class
 */
public class PerpedusApplication extends Application {

    private static PerpedusApplication instance;
    private boolean tabletVersion;
    private PlacesService placesService;

    public static PerpedusApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // save the instance
        instance = this;

        // init tablet version
        tabletVersion = getResources().getBoolean(R.bool.is_tablet);

        // init services
        placesService = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.BASIC).setEndpoint(Constants.PLACES_BASE_URL).build().create(PlacesService.class);

    }

    public boolean isTabletVersion() {
        return tabletVersion;
    }

    public PlacesService getPlacesService() {
        return placesService;
    }
}
