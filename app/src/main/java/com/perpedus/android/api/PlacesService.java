package com.perpedus.android.api;

import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.dom.PlacesResponse;
import com.perpedus.android.util.Constants;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Service interface that handles places calls
 */
public interface PlacesService {

    @GET(Constants.PLACES_SEARCH_URL)
    void getPlaces(@Query("location") String location,
                   @Query("radius") String radius,
                   @Query("language") String language,
                   @Query("keyword") String keyword,
                   @Query("type") String type,
                   @Query("key") String apiKey,
                   Callback<PlacesResponse> response);

    @GET(Constants.PLACE_DETAILS_SEARCH_URL)
    void getPlaceDetails(@Query("placeid") String placeId,
                         @Query("language") String language,
                         @Query("key") String apiKey,
                         Callback<PlaceDetailsResponse> response);

}
