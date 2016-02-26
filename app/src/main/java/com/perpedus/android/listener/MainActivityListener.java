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
     * @param types
     */
    void onSearchButtonPressed(String name, String radius, List<String> types);

    /**
     * Triggered when the user updates the place types from the dialog
     *
     * @param selectedTypes
     */
    void onPlaceTypesUpdated(List<String> selectedTypes);

    /**
     * Triggered when a new search language has been selected
     *
     * @param language
     */
    void onSearchLanguageSelected(String language);

    /**
     * Triggered when place details dialog was dismissed
     */
    void onPlaceDetailsDialogDismiss();
}
