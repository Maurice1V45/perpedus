package com.perpedus.android.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.perpedus.android.PerpedusApplication;

/**
 * Util class that handles shared preferences
 */
public class PreferencesUtils {

    /**
     * Retrieves the shared preferences
     *
     * @return
     */
    public static SharedPreferences getPreferences() {

        return PerpedusApplication.getInstance().getApplicationContext()
                .getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
    }


    /**
     * Stores a value into shared preferences
     *
     * @param key
     * @param value
     */
    public static void storePreference(String key, boolean value) {
        SharedPreferences preferences = getPreferences();
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * Stores a value into shared preferences
     *
     * @param key
     * @param value
     */
    public static void storePreference(String key, int value) {
        SharedPreferences preferences = getPreferences();
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     * Stores a value into shared preferences
     *
     * @param key
     * @param value
     */
    public static void storePreference(String key, long value) {
        SharedPreferences preferences = getPreferences();
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    /**
     * Stores a value into shared preferences
     *
     * @param key
     * @param value
     */
    public static void storePreference(String key, String value) {
        SharedPreferences preferences = getPreferences();
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
