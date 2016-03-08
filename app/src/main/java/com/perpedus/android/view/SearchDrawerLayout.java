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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.perpedus.android.R;
import com.perpedus.android.dialog.DialogUtils;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.PlacesHelper;

/**
 * Drawer layout that displays search options
 */
public class SearchDrawerLayout extends RelativeLayout {

    private MainActivityListener mainActivityListener;
    private EditText nameField;
    private SeekBar radiusSeekBar;
    private Button searchButton;
    private TextView radiusText;
    private String selectedType;
    private LayoutInflater inflater;
    private View editCategoryButton;
    private ImageView categoryIcon;
    private TextView categoryText;

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
        editCategoryButton = findViewById(R.id.edit_category_button);
        categoryIcon = (ImageView) findViewById(R.id.category_icon);
        categoryText = (TextView) findViewById(R.id.category_text);
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
                mainActivityListener.onSearchButtonPressed(name, radius, selectedType);
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

        editCategoryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // notify activity
                mainActivityListener.onPlaceTypesDialogOpen();

                // open dialog
                DialogUtils.showPlaceTypesDialog(((Activity) getContext()).getFragmentManager(), mainActivityListener);
            }
        });

    }

    /**
     * Initializes everything
     */
    private void initAll() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
        initListeners();
        initRadiusSeekBar();
        updateSelectedType();
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
        radiusText.setText((progress + 1) + " " + getContext().getString(R.string.drawer_km_text));
    }

    /**
     * Initializes radius seek bar
     */
    private void initRadiusSeekBar() {
        radiusSeekBar.setProgress(RADIUS_SEEK_BAR_INITIAL_POSITION);
        updateRadiusText(RADIUS_SEEK_BAR_INITIAL_POSITION);
    }

    /**
     * Updates selected type
     */
    public void updateSelectedType() {

        if (selectedType == null) {

            // set all place types icon and text
            categoryIcon.setImageResource(R.drawable.icon_stadium_white);
            categoryText.setText(R.string.types_all_types);

        } else {

            // set specific icon and text
            categoryIcon.setImageResource(PlacesHelper.getInstance().getWhitePlaceIcon(selectedType));
            categoryText.setText(PlacesHelper.getInstance().getPlaceTypeName(selectedType));

        }
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

}
