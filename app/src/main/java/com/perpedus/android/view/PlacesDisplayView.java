package com.perpedus.android.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.view.View;
import android.view.ViewTreeObserver;

import com.perpedus.android.R;
import com.perpedus.android.dom.Place;
import com.perpedus.android.dom.ScreenCoordinate;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.CoordinateProvider;
import com.perpedus.android.util.PlacesHelper;
import com.perpedus.android.util.ScreenUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Complex view that draws places on it
 */
public class PlacesDisplayView extends View {

    private Context context;
    private List<Place> places;
    private Place focusedPlace;

    // distance from the focused place pivot to the center of the screen
    private float focusedPlaceDistanceToCenter;
    private Location currentLocation;
    private MainActivityListener mainActivityListener;

    // size of the red or black marker icons
    private static final float MY_ICON_MARKER_SIZE = 256f;

    // size of the place type icons
    private static final float MY_PLACE_ICON_SIZE = 64f;

    // size of the arrow icons
    private static final float MY_ARROW_ICON_SIZE = 64f;

    // size of the meters text
    private static final float MY_METERS_TEXT_SIZE = 26f;

    // distance from where the focused line starts ( - this)
    private static final float MY_FOCUSED_LINE_NEGATIVE_MARGIN = 30f;

    // distance of how long the tick size of the focused line is
    private float MY_FOCUSED_LINE_TICK_SIZE;

    // top point of the details view
    private float MY_DETAILS_VIEW_POINT;

    private CoordinateProvider coordinateProvider;
    private float sensorX;
    private float sensorY;
    private float sensorZ;

    // coordinates of where the focused zone starts and ends
    private float focusedStartX;
    private float focusedStartY;
    private float focusedEndX;
    private float focusedEndY;

    public PlacesDisplayView(final Context context, final float cameraHorizontalAngle, final float cameraVerticalAngle, final MainActivityListener mainActivityListener) {
        super(context);
        this.context = context;
        this.mainActivityListener = mainActivityListener;
        this.places = new ArrayList<Place>();

        // initialize Coordinate provider after the view has been created
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                // initialize Coordinate provider
                coordinateProvider = new CoordinateProvider(context, getWidth(), getHeight(), cameraHorizontalAngle, cameraVerticalAngle);

                // set the start and end coordinates of the focused zone
                focusedStartX = getWidth() / 2f - getWidth() / 4f;
                focusedEndX = getWidth() / 2f + getWidth() / 4f;
                focusedStartY = getHeight() / 2f - getWidth() / 4f;
                focusedEndY = getHeight() / 2f + getWidth() / 4f;

                // set additional data
                MY_DETAILS_VIEW_POINT = getHeight() - ScreenUtils.fromDpToPixels(context, 144f);
                MY_FOCUSED_LINE_TICK_SIZE = getHeight() / 30f;

                // set closest and furthest location
                setClosestAndFurthestLocation();

                // notify Main Activity that this view has been created
                mainActivityListener.onPlacesDisplayViewCreated(getWidth(), getHeight());

                // remove the view tree observer
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    /**
     * This method will draw the places. This will also get called a lot. Like 10 times/sec
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // set focused place to null. We will determine the focused place later
        focusedPlace = null;

        for (Place place : places) {

            // set place screen coordinate
            ScreenCoordinate screenCoordinate = coordinateProvider.getScreenCoordinate(sensorX, sensorY, sensorZ, currentLocation, place.getLocation());
            place.setScreenCoordinate(screenCoordinate);

            // find focused places
            if (screenCoordinate.getOutOfBounds() == 0) {
                float pivotX = screenCoordinate.getWidth();
                float pivotY = screenCoordinate.getHeight();
                if (pivotX > focusedStartX && pivotX < focusedEndX && pivotY > focusedStartY && pivotY < focusedEndY) {

                    // place is in focus zone
                    if (focusedPlace == null) {

                        // set this place as the focused place
                        focusedPlace = place;
                        focusedPlaceDistanceToCenter = getDistanceToCenter(pivotX, pivotY);
                    } else {

                        // determine if this place is closer to the center than the currently
                        // focused place. If so, set this place as the focused place
                        float distanceToCenter = getDistanceToCenter(pivotX, pivotY);
                        if (distanceToCenter < focusedPlaceDistanceToCenter) {
                            focusedPlace = place;
                            focusedPlaceDistanceToCenter = distanceToCenter;
                        }
                    }
                }
            } else {

                // place is out of bounds. Draw the arrow
                drawArrow(canvas, place);
            }
        }

        for (Place place : places) {
            if (place != focusedPlace) {
                if (place.getScreenCoordinate().getOutOfBounds() == 0) {

                    // place is not out of bounds. Draw the place
                    drawPlace(canvas, place, false);

                }
            }
        }

        if (focusedPlace == null) {

            // notify Main Activity that there is no focused place
            mainActivityListener.onFocusedPlace(null);
        } else {

            // draw focused place
            drawPlace(canvas, focusedPlace, true);

            // notify Main Activity of the focused place
            mainActivityListener.onFocusedPlace(focusedPlace);
        }
    }

    /**
     * Draws the arrow since the place is out of bounds
     *
     * @param canvas
     * @param place
     */
    private void drawArrow(Canvas canvas, Place place) {

        // get screen coordinates
        ScreenCoordinate screenCoordinate = place.getScreenCoordinate();
        float pivotX = screenCoordinate.getWidth();
        float pivotY = screenCoordinate.getHeight();
        float arrowLeftMargin;
        float arrowTopMargin;
        Bitmap arrowBitmap;

        // adjust bitmap margins so that the bitmap is fully displayed on the screen
        switch (screenCoordinate.getOutOfBounds()) {
            case 1:
                arrowLeftMargin = pivotX;
                arrowTopMargin = pivotY - MY_ARROW_ICON_SIZE / 2f;
                break;
            case 2:
                arrowLeftMargin = pivotX;
                arrowTopMargin = pivotY;
                break;
            case 3:
                arrowLeftMargin = pivotX - MY_ARROW_ICON_SIZE / 2f;
                arrowTopMargin = pivotY;
                break;
            case 4:
                arrowLeftMargin = pivotX - MY_ARROW_ICON_SIZE;
                arrowTopMargin = pivotY;
                break;
            case 5:
                arrowLeftMargin = pivotX - MY_ARROW_ICON_SIZE;
                arrowTopMargin = pivotY - MY_ARROW_ICON_SIZE / 2f;
                break;
            case 6:
                arrowLeftMargin = pivotX - MY_ARROW_ICON_SIZE;
                arrowTopMargin = pivotY - MY_ARROW_ICON_SIZE;
                break;
            case 7:
                arrowLeftMargin = pivotX - MY_ARROW_ICON_SIZE / 2f;
                arrowTopMargin = pivotY - MY_ARROW_ICON_SIZE;
                break;
            case 8:
                arrowLeftMargin = pivotX;
                arrowTopMargin = pivotY - MY_ARROW_ICON_SIZE;
                break;
            default:
                arrowLeftMargin = pivotX;
                arrowTopMargin = pivotY;
                break;
        }

        // draw the bitmap
        arrowBitmap = PlacesHelper.getInstance().getArrowBitmap(screenCoordinate.getOutOfBounds());
        arrowLeftMargin = adjustArrowLeftMargin(arrowLeftMargin);
        arrowTopMargin = adjustArrowTopMargin(arrowTopMargin);
        canvas.drawBitmap(arrowBitmap, arrowLeftMargin, arrowTopMargin, new Paint());
    }


    /**
     * @param places the places to set
     */
    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    /**
     * Sets the closest and furthest location to the coordinate provicer
     */
    private void setClosestAndFurthestLocation() {
        float closest = Float.MAX_VALUE;
        float furthest = Float.MIN_VALUE;
        for (Place place : places) {
            float distance = currentLocation.distanceTo(place.getLocation());
            if (distance < closest) {
                closest = distance;
            }
            if (distance > furthest) {
                furthest = distance;
            }
        }
        coordinateProvider.setClosestLocation(closest);
        coordinateProvider.setFurthestLocation(furthest);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Returns the string value of the distance
     *
     * @param distance
     * @return
     */
    private String getDistanceDisplayValue(float distance) {
        if (distance < 1000) {

            // return distance in meters
            return Math.round(distance) + " m";
        } else {

            // distance will be in kilometers
            if (distance - Math.floor(distance) <= 0.1f) {

                // do not display ex: 3.0 km, display 3 km instead
                return Math.round(Math.floor(distance / 1000)) + " km";
            } else {

                // display the distance with 1 decimal
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                return decimalFormat.format(distance / 1000) + " km";
            }
        }
    }

    public void setSensorX(float sensorX) {
        this.sensorX = sensorX;
    }

    public void setSensorY(float sensorY) {
        this.sensorY = sensorY;
    }

    public void setSensorZ(float sensorZ) {
        this.sensorZ = sensorZ;
    }

    /**
     * Adjusts arrow left margin in case it's out of the screen
     *
     * @param arrowLeftMargin
     * @return
     */
    private float adjustArrowLeftMargin(float arrowLeftMargin) {
        if (arrowLeftMargin < 0) {
            return 0;
        } else if (arrowLeftMargin > getWidth() - MY_ARROW_ICON_SIZE) {
            return getWidth() - MY_ARROW_ICON_SIZE;
        } else {
            return arrowLeftMargin;
        }
    }

    /**
     * Adjusts arrow top margin in case it's out of the screen
     *
     * @param arrowTopMargin
     * @return
     */
    private float adjustArrowTopMargin(float arrowTopMargin) {
        if (arrowTopMargin < 0) {
            return 0;
        } else if (arrowTopMargin > getHeight() - MY_ARROW_ICON_SIZE) {
            return getHeight() - MY_ARROW_ICON_SIZE;
        } else {
            return arrowTopMargin;
        }
    }

    /**
     * Returns the distance to center from the given screen coordinates
     *
     * @param x
     * @param y
     * @return
     */
    private float getDistanceToCenter(float x, float y) {
        return (float) Math.sqrt(Math.pow((x - getWidth() / 2f), 2) + Math.pow((y - getHeight() / 2f), 2));
    }

    /**
     * Draws a place on the canvas
     *
     * @param canvas
     * @param place
     * @param isFocused
     */
    private void drawPlace(Canvas canvas, Place place, boolean isFocused) {

        // get pivot coordinates
        float pivotX = place.getScreenCoordinate().getWidth();
        float pivotY = place.getScreenCoordinate().getHeight();

        // get focused line height
        float focusedLineHeight = place.getFocusLineHeight();

        if (isFocused) {
            if (focusedLineHeight == 0f) {

                // start increasing the focused line height
                focusedLineHeight = pivotY + MY_ICON_MARKER_SIZE / 2f - MY_FOCUSED_LINE_NEGATIVE_MARGIN;
            } else {

                // increase the focused line height up to a limit (MY_DETAILS_VIEW_POINT)
                focusedLineHeight += MY_FOCUSED_LINE_TICK_SIZE;
                if (focusedLineHeight > MY_DETAILS_VIEW_POINT) {
                    focusedLineHeight = MY_DETAILS_VIEW_POINT;
                }
            }
        } else {
            if (focusedLineHeight < pivotY) {

                // set focused line height to 0
                focusedLineHeight = 0f;
            } else {

                // decrease focused line height up to 0
                focusedLineHeight -= MY_FOCUSED_LINE_TICK_SIZE;
                if (focusedLineHeight < 0f) {
                    focusedLineHeight = 0f;
                }
            }
        }
        place.setFocusLineHeight(focusedLineHeight);

        // draw focused line
        RectF focusedLineRect = new RectF(pivotX - 5f, pivotY + MY_ICON_MARKER_SIZE / 2f - MY_FOCUSED_LINE_NEGATIVE_MARGIN, pivotX, focusedLineHeight);
        Paint yellowPaint = new Paint();
        yellowPaint.setColor(getResources().getColor(R.color.yellow_light));
        canvas.drawRect(focusedLineRect, yellowPaint);

        // if focused line height has reached its peak, draw a triangle, else draw a circle
        if (focusedLineHeight == MY_DETAILS_VIEW_POINT) {

            // draw red triangle
            Point a = new Point((int) (pivotX - 12f), (int) MY_DETAILS_VIEW_POINT);
            Point b = new Point((int) (pivotX - 2f), (int) (MY_DETAILS_VIEW_POINT - 20f));
            Point c = new Point((int) (pivotX + 8f), (int) MY_DETAILS_VIEW_POINT);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.lineTo(a.x, a.y);
            path.close();
            canvas.drawPath(path, yellowPaint);


        } else if (focusedLineHeight != 0) {

            // draw circle
            canvas.drawCircle(pivotX - 1f, focusedLineHeight, 10f, yellowPaint);
        }

        // draw marker
        canvas.drawBitmap(PlacesHelper.getInstance().getMarkerBitmap(isFocused), pivotX - MY_ICON_MARKER_SIZE / 2f, pivotY - MY_ICON_MARKER_SIZE / 2f, new Paint());

        // draw place type icon
        if (!place.getTypes().isEmpty()) {
            Bitmap smallIcon = PlacesHelper.getInstance().getPlaceBitmap(place.getTypes().get(0));
            canvas.drawBitmap(smallIcon, pivotX - MY_PLACE_ICON_SIZE / 2f, pivotY - MY_PLACE_ICON_SIZE, new Paint());
        }

        // draw distance text
        float distance = currentLocation.distanceTo(place.getLocation());
        String metersText = getDistanceDisplayValue(distance);
        Paint metersPaint = new Paint();
        metersPaint.setTextSize(MY_METERS_TEXT_SIZE);
        metersPaint.setColor(isFocused ? getResources().getColor(R.color.grey_blue) : getResources().getColor(R.color.white));
        RectF metersAreaRect = new RectF(pivotX - MY_ICON_MARKER_SIZE / 2f, pivotY, pivotX + MY_ICON_MARKER_SIZE / 2f, pivotY + MY_ICON_MARKER_SIZE / 2.5f);
        RectF metersBounds = new RectF(metersAreaRect);
        metersBounds.right = metersPaint.measureText(metersText, 0, metersText.length());
        metersBounds.bottom = metersPaint.descent() - metersPaint.ascent();
        metersBounds.left += (metersAreaRect.width() - metersBounds.right) / 2.0f;
        metersBounds.top += (metersAreaRect.height() - metersBounds.bottom) / 2.0f;
        canvas.drawText(metersText, metersBounds.left, metersBounds.top - metersPaint.ascent(), metersPaint);
    }

}