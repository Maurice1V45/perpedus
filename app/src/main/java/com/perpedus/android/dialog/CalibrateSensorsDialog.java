package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.perpedus.android.R;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.PreferencesUtils;

/**
 * Informing dialog about calibrating sensors
 */
public class CalibrateSensorsDialog extends DialogFragment {

    private CheckBox neverShowAgainCheckbox;
    private View okButton;
    private MainActivityListener mainActivityListener;

    /**
     * Default constructor
     */
    public CalibrateSensorsDialog() {
        // empty constructor
    }

    /**
     * Views initializer
     *
     * @param rootView
     */
    private void initViews(View rootView) {
        neverShowAgainCheckbox = (CheckBox) rootView.findViewById(R.id.never_show_again_checkbox);
        okButton = rootView.findViewById(R.id.ok_button);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* remove dialog title */
        setStyle(STYLE_NO_TITLE, 0);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calibrate_sensors_dialog, container);
        initViews(view);
        initListeners();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return view;
    }

    /**
     * Listeners initializer
     */
    private void initListeners() {
        neverShowAgainCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.storePreference(Constants.PREF_SHOW_SENSORS_CALIBRATION_DIALOG, !isChecked);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // do stuff
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        // notify main activity
        mainActivityListener.onSensorsCalibrationDialogDismiss();
        super.onDismiss(dialog);
    }

    public void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
    }
}
