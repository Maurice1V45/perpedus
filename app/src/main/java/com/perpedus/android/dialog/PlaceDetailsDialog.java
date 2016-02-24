package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.perpedus.android.R;
import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.PreferencesUtils;
import com.perpedus.android.util.UrlUtils;
import com.squareup.picasso.Picasso;

/**
 * Dialog for place details
 */
public class PlaceDetailsDialog extends DialogFragment {

    /**
     * Async task that retrieves detailed data about a place
     */
    private class RetrievePlaceDetailsAsync extends AsyncTask<String, Void, PlaceDetailsResponse> {

        @Override
        protected PlaceDetailsResponse doInBackground(String... params) {

            // build the url to the place details web service
            String language = PreferencesUtils.getPreferences().getString(Constants.PREF_SELECTED_SEARCH_LANGUAGE, "en");
            String placeDetailsUrl = UrlUtils.buildPlaceDetailsLink(placeId, language);

            // get the response from the web service
            String placesJson = UrlUtils.getJsonFromUrl(placeDetailsUrl);
            PlaceDetailsResponse response = new Gson().fromJson(placesJson, PlaceDetailsResponse.class);

            return response;
        }

        @Override
        protected void onPostExecute(PlaceDetailsResponse response) {

            if (Constants.GOOGLE_RESPONSE_OK.equals(response.status)) {

                nameText.setText(response.result.name);
                addressText.setText(response.result.address);
                phoneText.setText(response.result.phone);
                if (response.result.photos != null) {
                    Picasso.with(getActivity()).load(UrlUtils.buildPlacePhotoLink(response.result.photos[0].reference)).into(image);
                }
            } else {

                // display error toast
                Toast.makeText(getActivity(), R.string.google_error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String placeId;
    private MainActivityListener mainActivityListener;
    private TextView nameText;
    private TextView addressText;
    private TextView phoneText;
    private ImageView image;


    /**
     * Default constructor
     */
    public PlaceDetailsDialog() {
        // empty constructor
    }

    /**
     * Views initializer
     *
     * @param rootView
     */
    private void initViews(View rootView) {
        nameText = (TextView) rootView.findViewById(R.id.name_text);
        addressText = (TextView) rootView.findViewById(R.id.address_text);
        phoneText = (TextView) rootView.findViewById(R.id.phone_text);
        image = (ImageView) rootView.findViewById(R.id.image);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* remove dialog title */
        setStyle(STYLE_NO_TITLE, 0);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_details_dialog, container);
        initViews(view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // make place details call
        RetrievePlaceDetailsAsync async = new RetrievePlaceDetailsAsync();
        async.execute();

        super.onViewCreated(view, savedInstanceState);
    }

    public void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
