package com.perpedus.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Settings activity
 */
public class SettingsActivity extends AppCompatActivity {

    private View creditsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        initViews();
        initListeners();
    }

    /**
     * Views initializer
     */
    private void initViews() {
        creditsButton = findViewById(R.id.credits_button);
    }

    /**
     * Listeners initializer
     */
    private void initListeners() {
        creditsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open credits activity
                Intent creditsIntent = new Intent(SettingsActivity.this, CreditsActivity.class);
                startActivity(creditsIntent);
            }
        });
    }
}
