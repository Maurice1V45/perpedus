package com.perpedus.android.dialog;

import android.app.FragmentManager;

import com.perpedus.android.listener.MainActivityListener;

import java.util.List;

/**
 * Util class for displaying dialogs
 */
public class DialogUtils {

    public static final String TAG_PLACE_TYPES_DIALOG = "tag_place_types_dialog";
    public static final String TAG_SEARCH_LANGUAGE_DIALOG = "tag_search_language_dialog";
    public static final String TAG_PLACE_DETAILS_DIALOG = "tag_place_details_dialog";

    public static void showPlaceTypesDialog(FragmentManager fm, MainActivityListener mainActivityListener, List<String> selectedTypes) {
        PlaceTypesDialog dialog = new PlaceTypesDialog();
        dialog.setSelectedTypes(selectedTypes);
        dialog.setMainActivityListener(mainActivityListener);
        dialog.show(fm, TAG_PLACE_TYPES_DIALOG);
    }

    public static void showSearchLanguageDialog(FragmentManager fm, MainActivityListener mainActivityListener) {
        SearchLanguageDialog dialog = new SearchLanguageDialog();
        dialog.setMainActivityListener(mainActivityListener);
        dialog.show(fm, TAG_SEARCH_LANGUAGE_DIALOG);
    }

    public static void showPlaceDetailsDialog(FragmentManager fm, MainActivityListener mainActivityListener, String placeId, int screenWidth) {
        PlaceDetailsDialog dialog = new PlaceDetailsDialog();
        dialog.setMainActivityListener(mainActivityListener);
        dialog.setPlaceId(placeId);
        dialog.setScreenWidth(screenWidth);
        dialog.show(fm, TAG_PLACE_DETAILS_DIALOG);
    }

}
