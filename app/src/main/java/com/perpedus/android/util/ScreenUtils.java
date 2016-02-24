package com.perpedus.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Util class that handles screen resolutions
 */
public class ScreenUtils {

    /**
     * Converts the given dp into pixels
     *
     * @param context
     * @param dp
     * @return
     */
    public static float fromDpToPixels(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

}
