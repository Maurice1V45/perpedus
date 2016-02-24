package com.perpedus.android.listener;

/**
 * Listener for place types dialog
 */
public interface PlaceTypesDialogListener {

    /**
     * Triggered when the user adds a place type
     *
     * @param placeType
     */
    void onPlaceTypeAdded(String placeType);

    /**
     * Triggered when the user removes a place type
     *
     * @param placeType
     */
    void onPlaceTypeRemoved(String placeType);

}
