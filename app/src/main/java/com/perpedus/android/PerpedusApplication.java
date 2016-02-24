package com.perpedus.android;

import android.app.Application;

/**
 * Application class
 */
public class PerpedusApplication extends Application {

    private static PerpedusApplication instance;
    private boolean tabletVersion;

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

    }

    public boolean isTabletVersion() {
        return tabletVersion;
    }
}
