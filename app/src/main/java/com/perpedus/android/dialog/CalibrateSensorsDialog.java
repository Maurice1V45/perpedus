package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perpedus.android.R;

/**
 * Informing dialog about calibrating sensors
 */
public class CalibrateSensorsDialog extends DialogFragment {


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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // do stuff
        super.onViewCreated(view, savedInstanceState);
    }

}
