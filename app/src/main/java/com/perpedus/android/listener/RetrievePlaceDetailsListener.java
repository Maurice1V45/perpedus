package com.perpedus.android.listener;

import com.perpedus.android.dom.PlaceDetailsResponse;

/**
 * Listeners for retrieving place details
 */
public interface RetrievePlaceDetailsListener {

    /**
     * Triggered when place details have been retrieved
     *
     * @param response
     */
    void onPlaceDetailsRetrieved(String response);

    /**
     * Triggered when place details retrieving gives an error
     */
    void onPlaceDetailsRetrievedError();

}
