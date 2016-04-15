package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.perpedus.android.R;
import com.perpedus.android.adapter.AppLanguageListAdapter;
import com.perpedus.android.listener.AppLanguageDialogListener;
import com.perpedus.android.listener.SettingsListener;
import com.perpedus.android.util.LanguageUtils;

import java.util.List;

/**
 * Dialog for app language picker
 */
public class AppLanguageDialog extends DialogFragment implements AppLanguageDialogListener {

    private RecyclerView languagesRecycler;
    private AppLanguageListAdapter adapter;
    private SettingsListener settingsListener;

    /**
     * Default constructor
     */
    public AppLanguageDialog() {
        // empty constructor
    }

    /**
     * Views initializer
     *
     * @param rootView
     */
    private void initViews(View rootView) {
        languagesRecycler = (RecyclerView) rootView.findViewById(R.id.languages_recycler_view);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* remove dialog title */
        setStyle(STYLE_NO_TITLE, 0);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_language_dialog, container);
        initViews(view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        List<String> languages = LanguageUtils.getAppLanguages(getActivity());

        // set adapter
        adapter = new AppLanguageListAdapter(getActivity(), languages, AppLanguageDialog.this);
        languagesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        languagesRecycler.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLanguageSelected(String language) {
        settingsListener.onAppLanguageUpdated(language);
        dismiss();
    }

    public void setSettingsListener(SettingsListener settingsListener) {
        this.settingsListener = settingsListener;
    }
}
