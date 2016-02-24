package com.perpedus.android.dom;


import android.location.Location;

import java.util.List;

/**
 * Object model that holds data about a place
 */
public class Place {

    private String placeId;
    private String name;
    private Location location;
    private List<String> types;
    private ScreenCoordinate screenCoordinate;
    private float focusLineHeight;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public ScreenCoordinate getScreenCoordinate() {
        return screenCoordinate;
    }

    public void setScreenCoordinate(ScreenCoordinate screenCoordinate) {
        this.screenCoordinate = screenCoordinate;
    }

    public float getFocusLineHeight() {
        return focusLineHeight;
    }

    public void setFocusLineHeight(float focusLineHeight) {
        this.focusLineHeight = focusLineHeight;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
