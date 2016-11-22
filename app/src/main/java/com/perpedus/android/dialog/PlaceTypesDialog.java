package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.util.LogWriter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.perpedus.android.PerpedusApplication;
import com.perpedus.android.R;
import com.perpedus.android.adapter.PlaceTypesGridAdapter;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.listener.PlaceTypesDialogListener;
import com.perpedus.android.util.PlacesHelper;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dialog for place type picker
 */
public class PlaceTypesDialog extends DialogFragment implements PlaceTypesDialogListener {

    private RecyclerView placeTypesRecycler;
    private List<String> placeTypes;
    private String selectedType;
    private MainActivityListener mainActivityListener;
    private PlaceTypesGridAdapter adapter;
    private Button allButton;
    private EditText categoryNameField;


    /**
     * Default constructor
     */
    public PlaceTypesDialog() {
        // empty constructor
    }

    /**
     * Views initializer
     *
     * @param rootView
     */
    private void initViews(View rootView) {
        placeTypesRecycler = (RecyclerView) rootView.findViewById(R.id.place_types_recycler_view);
        allButton = (Button) rootView.findViewById(R.id.all_button);
        categoryNameField = (EditText) rootView.findViewById(R.id.type_field);
    }

    /**
     * Listeners initializer
     */
    private void initListeners() {
        categoryNameField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter list
                adapter.setPlaceTypes(filterPlaceTypes(s.toString()));
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* remove dialog title */
        setStyle(STYLE_NO_TITLE, 0);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_types_dialog, container);
        initViews(view);
        initListeners();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        placeTypes = PlacesHelper.getInstance().getSearchablePlaceTypes(getActivity());

        // set adapter
        adapter = new PlaceTypesGridAdapter(getActivity(), placeTypes, PlaceTypesDialog.this);
        placeTypesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), PerpedusApplication.getInstance().isTabletVersion() ? 8 : 4));
        placeTypesRecycler.setAdapter(adapter);

        // set on button click listener
        allButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // notify activity
                mainActivityListener.onPlaceTypeUpdated(null);

                // dismiss dialog
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
    }

    @Override
    public void onPlaceTypeSelected(String placeType) {

        // notify activity
        mainActivityListener.onPlaceTypeUpdated(placeType);

        // dismiss dialog
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        // notify main activity
        mainActivityListener.onPlaceTypesDialogDismiss();

        super.onDismiss(dialog);
    }

    private List<String> filterPlaceTypes(String keyword) {
        List<String> filteredList = new ArrayList<String>();
        for (String placeTypeId : placeTypes) {

            // normalize place type and keyword
            String placeType = Normalizer.normalize(PlacesHelper.getInstance().getPlaceTypeName(placeTypeId), Normalizer.Form.NFD);
            placeType = placeType.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

            keyword = Normalizer.normalize(keyword, Normalizer.Form.NFD);
            keyword = keyword.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

            if (placeType.contains(keyword)) {
                filteredList.add(placeTypeId);
            }
        }
        return filteredList;
    }
}
