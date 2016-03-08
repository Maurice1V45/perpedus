package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.perpedus.android.R;
import com.perpedus.android.adapter.PhotosPagerAdapter;
import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.PreferencesUtils;
import com.perpedus.android.util.UrlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for place details
 */
public class PlaceDetailsDialog extends DialogFragment {


    private String placeId;
    private MainActivityListener mainActivityListener;
    private TextView nameText;
    private TextView addressText;
    private TextView phoneText;
    private ImageView image;
    private ViewPager photosViewPager;
    private PhotosPagerAdapter photosPagerAdapter;
    private int screenWidth;
    private LinearLayout reviewsLayout;
    private View noPhotosView;
    private LayoutInflater inflater;
    private PlaceDetailsResponse placeDetails;
    private View progressView;
    private View contentView;
    private boolean dataPopulated = false;

    private BroadcastReceiver placeDetailsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENT_PLACE_DETAILS_RECEIVED)) {
                Log.w("aaa", "received");

                // save place details
                if (intent.hasExtra(Constants.EXTRA_PLACE_DETAILS)) {
                    placeDetails = (PlaceDetailsResponse) intent.getSerializableExtra(Constants.EXTRA_PLACE_DETAILS);
                }

                if (placeDetails != null && !dataPopulated) {

                    // hide progress view and display data
                    progressView.setVisibility(View.GONE);
                    contentView.setVisibility(View.VISIBLE);
                    populateData();
                }
            }
        }
    };


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
        photosViewPager = (ViewPager) rootView.findViewById(R.id.photos_view_pager);
        reviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);
        noPhotosView = rootView.findViewById(R.id.no_photos_view);
        progressView = rootView.findViewById(R.id.progress_view);
        contentView = rootView.findViewById(R.id.content_view);
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
        this.inflater = inflater;
        initViews(view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (placeDetails != null && !dataPopulated) {

            // hide progress view and display data
            progressView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            populateData();
        }

        return view;
    }


    public void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
    }

    @Override
    public void onStart() {
        super.onStart();

        // register place details receiver
        getActivity().registerReceiver(placeDetailsReceiver, new IntentFilter(Constants.INTENT_PLACE_DETAILS_RECEIVED));
    }

    @Override
    public void onStop() {

        // unregister location receiver
        try {
            getActivity().unregisterReceiver(placeDetailsReceiver);
        } catch (Exception e) {
            // receiver was not registered
        }
        super.onStop();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        // notify activity
        mainActivityListener.onPlaceDetailsDialogDismiss();

        super.onDismiss(dialog);
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void updateReviewsLayout(PlaceDetailsResponse.Review[] reviews) {

        // remove all views from the layout
        reviewsLayout.removeAllViews();

        for (PlaceDetailsResponse.Review review : reviews) {

            // create a new view
            View listItem = inflater.inflate(R.layout.review_list_item, null);

            // set comment
            TextView commentText = (TextView) listItem.findViewById(R.id.comment_text);
            commentText.setText(review.author + " - " + review.comment);

            // add the view  to the layout
            reviewsLayout.addView(listItem);
        }
    }

    public void setPlaceDetails(PlaceDetailsResponse placeDetails) {
        this.placeDetails = placeDetails;
    }

    private void populateData() {

        Log.w("aaa", "populated");

        dataPopulated = true;

        nameText.setText(placeDetails.result.name);
        addressText.setText(placeDetails.result.address);
        phoneText.setText(placeDetails.result.phone);

        if (placeDetails.result.photos == null) {

            // show no photos view
            noPhotosView.setVisibility(View.VISIBLE);
            photosViewPager.setVisibility(View.GONE);

        } else {

            // show photos pager
            noPhotosView.setVisibility(View.GONE);
            photosViewPager.setVisibility(View.VISIBLE);

            // get all photo references
            List<String> photoReferences = new ArrayList<String>();
            for (PlaceDetailsResponse.Photo photo : placeDetails.result.photos) {
                photoReferences.add(photo.reference);
            }

            // set up photos adapter
            photosPagerAdapter = new PhotosPagerAdapter(getActivity(), photoReferences, screenWidth);
            photosViewPager.setAdapter(photosPagerAdapter);
        }

        // add reviews
        if (placeDetails.result.reviews != null) {
            updateReviewsLayout(placeDetails.result.reviews);
        }
    }
}
