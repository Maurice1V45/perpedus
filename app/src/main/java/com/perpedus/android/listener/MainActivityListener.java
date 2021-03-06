package com.perpedus.android.listener;

import com.perpedus.android.dom.Place;

import java.util.List;

/**
 * Listeners for Main Activity
 */
public interface MainActivityListener {

    /**
     * Triggered after PlacesDisplayView has been created
     *
     * @param width
     * @param height
     */
    void onPlacesDisplayViewCreated(int width, int height);

    /**
     * Triggered when a place enters or leaves focus zone
     *
     * @param focusedPlace - The place that is focused. Is null if no place is focused
     */
    void onFocusedPlace(Place focusedPlace);

    /**
     * Triggered when a search is submitted from the right drawer
     *
     * @param name
     * @param radius
     * @param type
     * @param onlyClosest
     */
    void onSearchButtonPressed(String name, String radius, String type, boolean onlyClosest);

    /**
     * Triggered when the user updates the place type from the dialog
     *
     * @param selectedType
     */
    void onPlaceTypeUpdated(String selectedType);

    /**
     * Triggered when place details dialog was dismissed
     */
    void onPlaceDetailsDialogDismiss();

    /**
     * Triggered when place types dialog was opened
     */
    void onPlaceTypesDialogOpen();

    /**
     * Triggered when place types dialog was dismissed
     */
    void onPlaceTypesDialogDismiss();

    /**
     * Triggered when sensors calibration dialog was dismissed
     */
    void onSensorsCalibrationDialogDismiss();


}
