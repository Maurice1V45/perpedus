package com.perpedus.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.listener.SettingsListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.LanguageUtils;
import com.perpedus.android.util.PreferencesUtils;

/**
 * Settings activity
 */
public class SettingsActivity extends AppCompatActivity implements SettingsListener{

    private View privacyPolicyButton;
    private View termsAndConditionsButton;
    private View creditsButton;
    private TextView selectedLanguageText;
    private View languageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        initViews();
        initListeners();
        initSelectedLanguage();

    }

    /**
     * Views initializer
     */
    private void initViews() {
        languageButton = findViewById(R.id.language_button);
        privacyPolicyButton = findViewById(R.id.privacy_policy_button);
        termsAndConditionsButton = findViewById(R.id.terms_and_conditions_button);
        creditsButton = findViewById(R.id.credits_button);
        selectedLanguageText = (TextView) findViewById(R.id.selected_language_text);
    }

    /**
     * Listeners initializer
     */
    private void initListeners() {
        languageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogUtils.showSearchLanguageDialog(getFragmentManager(), SettingsActivity.this);
            }
        });
        privacyPolicyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open privacy policy activity
                Intent creditsIntent = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
                startActivity(creditsIntent);
            }
        });
        termsAndConditionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open terms and conditions activity
                Intent creditsIntent = new Intent(SettingsActivity.this, TermsAndConditionsActivity.class);
                startActivity(creditsIntent);
            }
        });
        creditsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open credits activity
                Intent creditsIntent = new Intent(SettingsActivity.this, CreditsActivity.class);
                startActivity(creditsIntent);
            }
        });
    }

    private void initSelectedLanguage() {
        String selectedLanguage = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, Constants.DEFAULT_LANGUAGE);
        selectedLanguageText.setText(LanguageUtils.SUPPORTED_LANGUAGES_MAP.get(selectedLanguage));
    }

    @Override
    public void onSearchLanguageUpdated(String language) {

        // save language in shared preferences
        PreferencesUtils.storePreference(Constants.PREF_SELECTED_SEARCH_LANGUAGE, language);
        selectedLanguageText.setText(LanguageUtils.SUPPORTED_LANGUAGES_MAP.get(language));
    }
}
