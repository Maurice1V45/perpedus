package com.perpedus.android.util;

/**
 * Constants class
 */
public class Constants {

    /* URLS */
    public static final String URL_PRIVACY_POLICY = "http://www.perpedus.com/privacy-policy";
    public static final String URL_TERMS_AND_CONDITIONS = "http://www.perpedus.com/terms-and-conditions";

    /* Keys */
    public static final String PLACES_KEY = "AIzaSyCc7oA2cfSln6lorqam-4Jq5WFxzWQkbow";
    public static final String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place";
    public static final String PLACES_SEARCH_URL = "/nearbysearch/json";
    public static final String PLACE_DETAILS_SEARCH_URL = "/details/json";
    public static final String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo";
    public static final String GOOGLE_RESPONSE_OK = "OK";
    public static final String GOOGLE_RESPONSE_NO_RESULTS = "ZERO_RESULTS";

    /* Preferences */
    public static final String PREFERENCES = "prefs";
    public static final String PREF_SELECTED_SEARCH_LANGUAGE = "pref_selected_search_language";
    public static final String PREF_SELECTED_APP_LANGUAGE = "pref_selected_app_language";
    public static final String PREF_TUTORIAL_COMPLETED = "pref_tutorial_completed";

    /* Intents */
    public static final String INTENT_LOCATION_ENABLED_RECEIVED = "android.location.PROVIDERS_CHANGED";
    public static final String INTENT_LOCATION_UPDATED = "com.perpedus.android.LOCATION_UPDATED";
    public static final String INTENT_PLACE_DETAILS_RECEIVED = "com.perpedus.android.PLACE_DETAILS_RECEIVED";

    /* Extras */
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_PROVIDER = "extra_provider";
    public static final String EXTRA_PLACES = "extra_places";
    public static final String EXTRA_PLACE_DETAILS = "extra_place_details";

    /* Other */
    public static final String DEFAULT_LANGUAGE = "en";

}
