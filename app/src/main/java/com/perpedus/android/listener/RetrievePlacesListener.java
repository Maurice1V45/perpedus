package com.perpedus.android.listener;

import com.perpedus.android.dom.Place;

import java.util.List;

/**
 * Listeners for retrieving places
 */
public interface RetrievePlacesListener {

    /**
     * Triggered when new places have been retrieved
     *
     * @param response
     */
    void onPlacesRetrieved(String response);

    /**
     * Triggered when place retrieving gives an error
     */
    void onPlacesRetrievedError();

}
