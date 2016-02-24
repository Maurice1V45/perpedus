package com.perpedus.android.comparator;

import android.content.Context;

import com.perpedus.android.util.LanguageUtils;

import java.util.Comparator;

/**
 * Comparator used to sort languages alphabetically
 */
public class LanguagesComparator implements Comparator<String> {

    Context context;

    /**
     * Constructor
     *
     * @param context
     */
    public LanguagesComparator(Context context) {
        this.context = context;
    }

    @Override
    public int compare(String leftEntry, String rightEntry) {
        return context.getString(LanguageUtils.SUPPORTED_LANGUAGES_MAP.get(leftEntry)).compareTo(context.getString(LanguageUtils.SUPPORTED_LANGUAGES_MAP.get(rightEntry)));
    }
}
