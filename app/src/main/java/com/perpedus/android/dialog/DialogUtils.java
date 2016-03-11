package com.perpedus.android.dialog;

import android.app.FragmentManager;

import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.listener.SettingsListener;

import java.util.List;

/**
 * Util class for displaying dialogs
 */
public class DialogUtils {

    public static final String TAG_PLACE_TYPES_DIALOG = "tag_place_types_dialog";
    public static final String TAG_SEARCH_LANGUAGE_DIALOG = "tag_search_language_dialog";
    public static final String TAG_PLACE_DETAILS_DIALOG = "tag_place_details_dialog";
    public static final String TAG_CALIBRATE_SENSORS_DIALOG = "tag_calibrate_sensors_dialog";

    public static void showPlaceTypesDialog(FragmentManager fm, MainActivityListener mainActivityListener) {
        PlaceTypesDialog dialog = new PlaceTypesDialog();
        dialog.setMainActivityListener(mainActivityListener);
        dialog.show(fm, TAG_PLACE_TYPES_DIALOG);
    }

    public static void showSearchLanguageDialog(FragmentManager fm, SettingsListener settingsListener) {
        SearchLanguageDialog dialog = new SearchLanguageDialog();
        dialog.setSettingsListener(settingsListener);
        dialog.show(fm, TAG_SEARCH_LANGUAGE_DIALOG);
    }

    public static void showPlaceDetailsDialog(FragmentManager fm, MainActivityListener mainActivityListener, PlaceDetailsResponse placeDetails, int screenWidth) {
        PlaceDetailsDialog dialog = new PlaceDetailsDialog();
        dialog.setMainActivityListener(mainActivityListener);
        dialog.setPlaceDetails(placeDetails);
        dialog.setScreenWidth(screenWidth);
        dialog.show(fm, TAG_PLACE_DETAILS_DIALOG);
    }

    public static void showCalibrateSensorsDialog(FragmentManager fm) {
        CalibrateSensorsDialog dialog = new CalibrateSensorsDialog();
        dialog.show(fm, TAG_CALIBRATE_SENSORS_DIALOG);
    }

}
