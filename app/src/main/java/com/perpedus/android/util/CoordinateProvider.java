package com.perpedus.android.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.perpedus.android.dom.ScreenCoordinate;

/**
 * Helper class that provides screen coordinates
 */
public class CoordinateProvider {

    private Context context;
    private int screenWidth;
    private int screenHeight;
    private float cameraHorizontalAngle;
    private float cameraVerticalAngle;
    private float closestLocation;
    private float furthestLocation;

    /**
     * Constructor
     *
     * @param context
     * @param screenWidth           - Width of the screen
     * @param screenHeight          - Height of the screen
     * @param cameraHorizontalAngle - Portrait angle of the camera
     * @param cameraVerticalAngle   - Landscape angle of the camera
     */
    public CoordinateProvider(Context context, int screenWidth, int screenHeight, float cameraHorizontalAngle, float cameraVerticalAngle) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.cameraHorizontalAngle = (cameraHorizontalAngle < 30 || cameraHorizontalAngle > 90) ? 40 : cameraHorizontalAngle;
        this.cameraVerticalAngle = (cameraVerticalAngle < 30 || cameraVerticalAngle > 90) ? 50 : cameraVerticalAngle;
//        Log.w("prop", "width=" + screenWidth);
//        Log.w("prop", "height=" + screenHeight);
//        Log.w("prop", "horiz angle=" + cameraHorizontalAngle);
//        Log.w("prop", "vert angle=" + cameraVerticalAngle);
    }

    /**
     * Returns a ScreenCoordinate for a given list of parameters
     *
     * @param sensorX        - Sensor X coordinate
     * @param sensorY        - Sensor Y coordinate
     * @param sensorZ        - Sensor Z coordinate
     * @param myLocation     - Current location
     * @param targetLocation - Location of the target Place
     * @return
     */
    public ScreenCoordinate getScreenCoordinate(float sensorX, float sensorY, float sensorZ, Location myLocation, Location targetLocation) {

        ScreenCoordinate screenCoordinate = new ScreenCoordinate();

        // calculate the bearing. Bearing represents the angle from the current location to the
        // target location considering we are facing north
        float bearing = myLocation.bearingTo(targetLocation);
        if (bearing < 0) {
            bearing += 360f;
        }

        // calculate the angle. Angle represents the angle from the current location to the
        // target location considering we are facing the X angle
        float angle = CoordinateUtils.getAngleForDegrees(sensorX, bearing);

        // calculate distance delta. The distance delta adds or removes some extra Y pixels
        // depending on how close/far the target place is
        float distanceDelta = 0f;
        if (furthestLocation != 0 && closestLocation != furthestLocation) {
            float distance = myLocation.distanceTo(targetLocation);
            distanceDelta = ((distance * screenHeight / 4f / furthestLocation) - (screenHeight / 8f)) * -1;
        }

        // calculate width and height. These are the coordinates used to display the Place on
        // the screen
        float width = (screenWidth / 2f) + (angle * screenWidth / cameraHorizontalAngle);
        float height = (screenHeight / 2f) - ((sensorY + 90f) * screenHeight / cameraVerticalAngle) + distanceDelta;

        // if the width and height are out of screen bounds, set the outOfBounds value accordingly
        if (width <= 0) {
            screenCoordinate.setWidth(0);
            if (height < 0) {
                screenCoordinate.setOutOfBounds(2);
                screenCoordinate.setHeight(0);
            } else if (height > screenHeight) {
                screenCoordinate.setOutOfBounds(8);
                screenCoordinate.setHeight(screenHeight);
            } else {
                screenCoordinate.setOutOfBounds(1);
                screenCoordinate.setHeight(height);
            }
        } else if (width > screenWidth) {
            screenCoordinate.setWidth(screenWidth);
            if (height < 0) {
                screenCoordinate.setOutOfBounds(4);
                screenCoordinate.setHeight(0);
            } else if (height > screenHeight) {
                screenCoordinate.setOutOfBounds(6);
                screenCoordinate.setHeight(screenHeight);
            } else {
                screenCoordinate.setOutOfBounds(5);
                screenCoordinate.setHeight(height);
            }
        } else {
            screenCoordinate.setWidth(width);
            if (height < 0) {
                screenCoordinate.setOutOfBounds(3);
                screenCoordinate.setHeight(0);
            } else if (height > screenHeight) {
                screenCoordinate.setOutOfBounds(7);
                screenCoordinate.setHeight(screenHeight);
            } else {
                screenCoordinate.setOutOfBounds(0);
                screenCoordinate.setHeight(height);
            }
        }

        return screenCoordinate;
    }


    public void setClosestLocation(float closestLocation) {
        this.closestLocation = closestLocation;
    }

    public void setFurthestLocation(float furthestLocation) {
        this.furthestLocation = furthestLocation;
    }



}
