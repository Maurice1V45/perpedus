package com.perpedus.android.dom;

/**
 * Object data that represents a screen coordinate for a place
 */
public class ScreenCoordinate {

    /**
     * 0 - left
     * screenWidth - right
     */
    private float width;

    /**
     * 0 - top
     * screenHeight - bottom
     */
    private float height;

    /**
     * 0 - place is not out of bounds (is visible on the screen)
     * 1 - left
     * 2 - top left
     * 3 - top
     * 4 - top right
     * 5 - right
     * 6 - bottom right
     * 7 - bottom
     * 8 - bottom left
     */
    private int outOfBounds;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getOutOfBounds() {
        return outOfBounds;
    }

    public void setOutOfBounds(int outOfBounds) {
        this.outOfBounds = outOfBounds;
    }
}
