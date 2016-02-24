package com.perpedus.android.comparator;

import android.location.Location;

import com.perpedus.android.dom.Place;

import java.util.Comparator;

/**
 * Comparator used to sort places by distance (closest to furthest)
 */
public class PlacesComparator implements Comparator<Place> {

    private Location myLocation;

    /**
     * Constructor
     *
     * @param myLocation
     */
    public PlacesComparator(Location myLocation) {
        this.myLocation = myLocation;
    }

    @Override
    public int compare(Place leftPlace, Place rightPlace) {
        return myLocation.distanceTo(leftPlace.getLocation()) < myLocation.distanceTo(rightPlace.getLocation()) ? 1 : -1;
    }
}
