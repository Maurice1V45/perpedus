package com.perpedus.android.util;

import android.location.Location;

import com.perpedus.android.dom.Place;
import com.perpedus.android.dom.PlacesResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Util class that converts objects into other objects
 */
public class ConverterUtils {

    /**
     * Converts a PlacesResponse to a list of Places
     *
     * @param response
     * @return
     */
    public static List<Place> fromResponseToPlaces(PlacesResponse response) {

        List<Place> places = new ArrayList<Place>();
        for (PlacesResponse.Result result : response.results) {

            // create a new Place
            Place place = new Place();

            // set place id
            place.setPlaceId(result.placeId);

            // set the name
            place.setName(result.name);

            // set the location
            double latitude = result.geometry.location.latitude;
            double longitude = result.geometry.location.longitude;
            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            place.setLocation(location);

            // set the place type
            place.setTypes(Arrays.asList(result.types));

            // add the place to the list
            places.add(place);
        }

        return places;
    }
}
