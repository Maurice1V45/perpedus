package com.perpedus.android.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.perpedus.android.R;
import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.PlacesHelper;
import com.perpedus.android.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Drawer layout that displays search options
 */
public class SearchDrawerLayout extends RelativeLayout {

    private MainActivityListener mainActivityListener;
    private EditText nameField;
    private SeekBar radiusSeekBar;
    private Button searchButton;
    private TextView radiusText;
    private View placeTypesButton;
    private List<String> selectedTypes;
    private LinearLayout selectedTypesLayout;
    private LayoutInflater inflater;
    private Button searchLanguageButton;

    // set to 4 so that initial search will be on 5 km
    private static final int RADIUS_SEEK_BAR_INITIAL_POSITION = 4;

    /**
     * Constructor
     *
     * @param context
     */
    public SearchDrawerLayout(Context context) {
        super(context);
        initAll();
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public SearchDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SearchDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAll();
    }

    /**
     * Views initializer
     */
    private void initViews() {
        inflate(getContext(), R.layout.search_drawer_layout, SearchDrawerLayout.this);
        nameField = (EditText) findViewById(R.id.location_name_field);
        radiusSeekBar = (SeekBar) findViewById(R.id.radius_seek_bar);
        searchButton = (Button) findViewById(R.id.search_button);
        radiusText = (TextView) findViewById(R.id.radius_text);
        placeTypesButton = findViewById(R.id.place_types_button);
        selectedTypesLayout = (LinearLayout) findViewById(R.id.selected_types_layout);
        searchLanguageButton = (Button) findViewById(R.id.search_language_button);
    }

    /**
     * Listeners initializer
     */
    private void initListeners() {

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String radius = String.valueOf((long) (radiusSeekBar.getProgress() + 1) * 1000);
                mainActivityListener.onSearchButtonPressed(name, radius, selectedTypes);
            }
        });

        radiusSeekBar.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow Drawer to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow Drawer to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateRadiusText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing to do here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // nothing to do here
            }
        });

        placeTypesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // create a copy of selectedTypes, because it will get modified
                List<String> selectedTypesCopy = new ArrayList<String>();
                for (String type : selectedTypes) {
                    selectedTypesCopy.add(type);
                }

                // open dialog
                DialogUtils.showPlaceTypesDialog(((Activity) getContext()).getFragmentManager(), mainActivityListener, selectedTypesCopy);
            }
        });

        searchLanguageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogUtils.showSearchLanguageDialog(((Activity) getContext()).getFragmentManager(), mainActivityListener);
            }
        });
    }

    /**
     * Initializes everything
     */
    private void initAll() {
        initViews();
        initListeners();
        initRadiusSeekBar();
        updateSearchLanguageButton();
        selectedTypes = new ArrayList<String>();
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
    }

    /**
     * Updates radius text according to radius seek bar progress
     *
     * @param progress
     */
    private void updateRadiusText(int progress) {
        radiusText.setText((progress + 1) + " " + getContext().getString(R.string.km));
    }

    /**
     * Initializes radius seek bar
     */
    private void initRadiusSeekBar() {
        radiusSeekBar.setProgress(RADIUS_SEEK_BAR_INITIAL_POSITION);
        updateRadiusText(RADIUS_SEEK_BAR_INITIAL_POSITION);
    }

    /**
     * Updates selected types layout
     */
    public void updateSelectedTypes() {

        // remove all views from the layout
        selectedTypesLayout.removeAllViews();

        for (String selectedType : selectedTypes) {

            // create a new view
            View listItem = inflater.inflate(R.layout.selected_type_list_item, null);

            // set icon
            ImageView icon = (ImageView) listItem.findViewById(R.id.icon);
            icon.setImageResource(PlacesHelper.getInstance().getPlaceIcon(selectedType));

            // set title
            TextView title = (TextView) listItem.findViewById(R.id.title);
            title.setText(PlacesHelper.getInstance().getPlaceTypeName(selectedType));

            // add the view  to the layout
            selectedTypesLayout.addView(listItem);
        }
    }

    public void setSelectedTypes(List<String> selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public void updateSearchLanguageButton() {

        // get language
        String searchLanguage = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, "");

        // if empty, set default to english
        if (searchLanguage.isEmpty()) {
            searchLanguage = "en";
            PreferencesUtils.storePreference(Constants.PREF_SELECTED_SEARCH_LANGUAGE, searchLanguage);
        }

        // set button text
        searchLanguageButton.setText(searchLanguage);
    }
}
