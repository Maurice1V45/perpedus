package com.perpedus.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.listener.SettingsListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.LanguageUtils;
import com.perpedus.android.util.LocalizationUtils;
import com.perpedus.android.util.PreferencesUtils;

/**
 * Settings activity
 */
public class LanguageCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check app language
        String appLanguage = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_APP_LANGUAGE, "");
        if (appLanguage.isEmpty()) {

            // set default language
            String language = LocalizationUtils.getLanguage();
            PreferencesUtils.storePreference(Constants.PREF_SELECTED_APP_LANGUAGE, language);
            LocalizationUtils.changeLanguage(LanguageCheckActivity.this, language);

        } else {
            LocalizationUtils.changeLanguage(LanguageCheckActivity.this, appLanguage);
        }

        // launch loading activity
        Intent refresh = new Intent(LanguageCheckActivity.this, LoadingActivity.class);
        startActivity(refresh);
        finish();

    }

}
