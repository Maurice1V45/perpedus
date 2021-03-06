package com.perpedus.android.listener;

/**
 * Listeners for settings
 */
public interface SettingsListener {

    /**
     * Triggered when search language has been changed
     *
     * @param language
     */
    void onSearchLanguageUpdated(String language);

    /**
     * Triggered when app language has been changed
     *
     * @param language
     */
    void onAppLanguageUpdated(String language);


}
