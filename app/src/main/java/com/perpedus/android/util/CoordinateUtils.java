package com.perpedus.android.util;

/**
 * Created by Marius on 01-Nov-16.
 */

public class CoordinateUtils {

    /**
     * Calculates the angle between 2 (0, 360) coordinates. The result will be in a (-180, 180)
     * range
     *
     * @param a
     * @param b
     * @return
     */
    public static float getAngleForDegrees(float a, float b) {
        float d = Math.abs(a - b) % 360;
        float r = d > 180 ? 360 - d : d;
        int sign = (a - b >= 0 && a - b <= 180) || (a - b <= -180 && a- b>= -360) ? 1 : -1;
        r *= -sign;
        return r;
    }
}
