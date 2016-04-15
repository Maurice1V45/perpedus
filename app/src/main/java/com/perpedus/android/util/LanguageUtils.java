package com.perpedus.android.util;

import android.content.Context;

import com.perpedus.android.R;
import com.perpedus.android.comparator.LanguagesComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class for handling languages
 */
public class LanguageUtils {

    /* Supported languages map */
    public static final Map<String, Integer> SUPPORTED_LANGUAGES_MAP = new HashMap<String, Integer>();

    static {
        SUPPORTED_LANGUAGES_MAP.put("ar", R.string.language_arabic);
        SUPPORTED_LANGUAGES_MAP.put("bg", R.string.language_bulgarian);
        SUPPORTED_LANGUAGES_MAP.put("bn", R.string.language_bengali);
        SUPPORTED_LANGUAGES_MAP.put("ca", R.string.language_catalan);
        SUPPORTED_LANGUAGES_MAP.put("cs", R.string.language_czech);
        SUPPORTED_LANGUAGES_MAP.put("da", R.string.language_danish);
        SUPPORTED_LANGUAGES_MAP.put("de", R.string.language_german);
        SUPPORTED_LANGUAGES_MAP.put("el", R.string.language_greek);
        SUPPORTED_LANGUAGES_MAP.put("en", R.string.language_english);
        SUPPORTED_LANGUAGES_MAP.put("en-AU", R.string.language_english_australian);
        SUPPORTED_LANGUAGES_MAP.put("en-GB", R.string.language_english_gb);
        SUPPORTED_LANGUAGES_MAP.put("es", R.string.language_spanish);
        SUPPORTED_LANGUAGES_MAP.put("eu", R.string.language_basque);
        SUPPORTED_LANGUAGES_MAP.put("fa", R.string.language_farsi);
        SUPPORTED_LANGUAGES_MAP.put("fi", R.string.language_finnish);
        SUPPORTED_LANGUAGES_MAP.put("fil", R.string.language_filipino);
        SUPPORTED_LANGUAGES_MAP.put("fr", R.string.language_french);
        SUPPORTED_LANGUAGES_MAP.put("gl", R.string.language_galician);
        SUPPORTED_LANGUAGES_MAP.put("gu", R.string.language_gujarati);
        SUPPORTED_LANGUAGES_MAP.put("hi", R.string.language_hindi);
        SUPPORTED_LANGUAGES_MAP.put("hr", R.string.language_croatian);
        SUPPORTED_LANGUAGES_MAP.put("hu", R.string.language_hungarian);
        SUPPORTED_LANGUAGES_MAP.put("id", R.string.language_indonesian);
        SUPPORTED_LANGUAGES_MAP.put("it", R.string.language_italian);
        SUPPORTED_LANGUAGES_MAP.put("iw", R.string.language_hebrew);
        SUPPORTED_LANGUAGES_MAP.put("ja", R.string.language_japanese);
        SUPPORTED_LANGUAGES_MAP.put("kn", R.string.language_kannada);
        SUPPORTED_LANGUAGES_MAP.put("ko", R.string.language_korean);
        SUPPORTED_LANGUAGES_MAP.put("lt", R.string.language_lithuanian);
        SUPPORTED_LANGUAGES_MAP.put("lv", R.string.language_latvian);
        SUPPORTED_LANGUAGES_MAP.put("ml", R.string.language_malayalam);
        SUPPORTED_LANGUAGES_MAP.put("mr", R.string.language_marathi);
        SUPPORTED_LANGUAGES_MAP.put("nl", R.string.language_dutch);
        SUPPORTED_LANGUAGES_MAP.put("no", R.string.language_norwegian);
        SUPPORTED_LANGUAGES_MAP.put("pl", R.string.language_polish);
        SUPPORTED_LANGUAGES_MAP.put("pt", R.string.language_portugese);
        SUPPORTED_LANGUAGES_MAP.put("pt-BR", R.string.language_portugese_brazil);
        SUPPORTED_LANGUAGES_MAP.put("pt-PT", R.string.language_portugese_portugal);
        SUPPORTED_LANGUAGES_MAP.put("ro", R.string.language_romanian);
        SUPPORTED_LANGUAGES_MAP.put("ru", R.string.language_russian);
        SUPPORTED_LANGUAGES_MAP.put("sk", R.string.language_slovak);
        SUPPORTED_LANGUAGES_MAP.put("sl", R.string.language_slovenian);
        SUPPORTED_LANGUAGES_MAP.put("sr", R.string.language_serbian);
        SUPPORTED_LANGUAGES_MAP.put("sv", R.string.language_swedish);
        SUPPORTED_LANGUAGES_MAP.put("ta", R.string.language_tamil);
        SUPPORTED_LANGUAGES_MAP.put("te", R.string.language_telugu);
        SUPPORTED_LANGUAGES_MAP.put("th", R.string.language_thai);
        SUPPORTED_LANGUAGES_MAP.put("tl", R.string.language_tagalog);
        SUPPORTED_LANGUAGES_MAP.put("tr", R.string.language_turkish);
        SUPPORTED_LANGUAGES_MAP.put("uk", R.string.language_ukrainian);
        SUPPORTED_LANGUAGES_MAP.put("vi", R.string.language_vietnamese);
        SUPPORTED_LANGUAGES_MAP.put("zh-CN", R.string.language_chinese_simplified);
        SUPPORTED_LANGUAGES_MAP.put("zh-TW", R.string.language_chinese_traditional);
    }

    public static final Map<String, Integer> APP_LANGUAGES_MAP = new HashMap<String, Integer>();

    static {
        APP_LANGUAGES_MAP.put("en", R.string.language_english);
        APP_LANGUAGES_MAP.put("ro", R.string.language_romanian);
    }

    public static List<String> getSupportedLanguages(Context context) {

        // get all supported languages
        List<String> supportedLanguages = new ArrayList<String>();
        for (String key : SUPPORTED_LANGUAGES_MAP.keySet()) {
            supportedLanguages.add(key);
        }

        // sort languages alphabetically
        Collections.sort(supportedLanguages, new LanguagesComparator(context));

        return supportedLanguages;
    }

    public static List<String> getAppLanguages(Context context) {

        // get all app languages
        List<String> appLanguages = new ArrayList<String>();
        for (String key : APP_LANGUAGES_MAP.keySet()) {
            appLanguages.add(key);
        }

        // sort languages alphabetically
        Collections.sort(appLanguages, new LanguagesComparator(context));

        return appLanguages;
    }

    /**
     * Checks if the given language is supported by Google Places
     *
     * @param language
     * @return
     */
    public static boolean isLanguageSupported(String language) {
        for (String key : SUPPORTED_LANGUAGES_MAP.keySet()) {
            if (key.equals(language)) {
                return true;
            }
        }
        return false;
    }
}
