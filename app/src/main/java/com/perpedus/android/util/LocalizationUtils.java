package com.perpedus.android.util;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;


/**
 * Localization util class
 */
public class LocalizationUtils {

    private static final String ROMANIAN = "ro";
    private static final String ENGLISH = "en";


    /**
     * Returns the language of the app based on the phone language. English language is default.
     *
     * @return
     */
    public static String getLanguage() {
        String locale = Locale.getDefault().toString();
        if (locale.startsWith(ROMANIAN)) {
            return ROMANIAN;
        } else {
            return ENGLISH;
        }
    }

    public static void changeLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }
}
