package com.perpedus.android.comparator;

import android.content.Context;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator used to sort places types alphabetically
 */
public class PlaceTypesComparator implements Comparator<Map.Entry<String, Object[]>> {

    Context context;

    /**
     * Constructor
     *
     * @param context
     */
    public PlaceTypesComparator(Context context) {
        this.context = context;
    }

    @Override
    public int compare(Map.Entry<String, Object[]> leftEntry, Map.Entry<String, Object[]> rightEntry) {
        return context.getString((int) leftEntry.getValue()[0]).compareTo(context.getString((int) rightEntry.getValue()[0]));
    }
}
