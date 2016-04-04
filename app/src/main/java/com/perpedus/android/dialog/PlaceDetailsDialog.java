package com.perpedus.android.dialog;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.perpedus.android.R;
import com.perpedus.android.adapter.PhotosPagerAdapter;
import com.perpedus.android.adapter.ReviewsPagerAdapter;
import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.listener.MainActivityListener;
import com.perpedus.android.util.Constants;
import com.perpedus.android.util.PreferencesUtils;
import com.perpedus.android.util.UrlUtils;
import com.perpedus.android.view.CustomToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Dialog for place details
 */
public class PlaceDetailsDialog extends DialogFragment {


    private String placeId;
    private MainActivityListener mainActivityListener;
    private TextView nameText;
    private TextView addressText;
    private TextView phoneText;
    private ViewPager photosViewPager;
    private ViewPager reviewsViewPager;
    private PhotosPagerAdapter photosPagerAdapter;
    private ReviewsPagerAdapter reviewsPagerAdapter;
    private int screenWidth;
    private View noPhotosView;
    private CircleIndicator photosPagerIndicator;
    private LayoutInflater inflater;
    private PlaceDetailsResponse placeDetails;
    private View progressView;
    private View contentView;
    private View reviewsLeftArrow;
    private View reviewsRightArrow;
    private View reviewsLayout;
    private boolean dataPopulated = false;

    private BroadcastReceiver placeDetailsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENT_PLACE_DETAILS_RECEIVED)) {

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
        photosViewPager = (ViewPager) rootView.findViewById(R.id.photos_view_pager);
        reviewsViewPager = (ViewPager) rootView.findViewById(R.id.reviews_view_pager);
        photosPagerIndicator = (CircleIndicator) rootView.findViewById(R.id.photos_pager_indicator);
        noPhotosView = rootView.findViewById(R.id.no_photos_view);
        progressView = rootView.findViewById(R.id.progress_view);
        contentView = rootView.findViewById(R.id.content_view);
        reviewsLeftArrow = rootView.findViewById(R.id.reviews_left_arrow);
        reviewsRightArrow = rootView.findViewById(R.id.reviews_right_arrow);
        reviewsLayout = rootView.findViewById(R.id.reviews_layout);
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
        initListeners();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (placeDetails != null && !dataPopulated) {

            // hide progress view and display data
            progressView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            populateData();
        }

        return view;
    }

    private void initListeners() {

        reviewsLeftArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reviewsViewPager.setCurrentItem(reviewsViewPager.getCurrentItem() - 1, true);
            }
        });

        reviewsRightArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reviewsViewPager.setCurrentItem(reviewsViewPager.getCurrentItem() + 1, true);
            }
        });

        reviewsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // nothing to do here
            }

            @Override
            public void onPageSelected(int position) {
                updateReviewsArrows();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // nothing to do here
            }
        });
    }

    private void updateReviewsArrows() {
        int currentPosition = reviewsViewPager.getCurrentItem();

        if (currentPosition == 0) {
            reviewsLeftArrow.setEnabled(false);
        } else {
            reviewsLeftArrow.setEnabled(true);
        }

        if (currentPosition == placeDetails.result.reviews.length - 1) {
            reviewsRightArrow.setEnabled(false);
        } else {
            reviewsRightArrow.setEnabled(true);
        }
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

    public void setPlaceDetails(PlaceDetailsResponse placeDetails) {
        this.placeDetails = placeDetails;
    }

    private void populateData() {

        dataPopulated = true;

        nameText.setText(placeDetails.result.name);
        addressText.setText(placeDetails.result.address);
        if (placeDetails.result.formattedPhone == null || placeDetails.result.phone == null) {

            // set no phone text
            phoneText.setText(R.string.place_details_no_phone);
            phoneText.setBackgroundResource(0);
            phoneText.setCompoundDrawables(null, null, null, null);

        } else {

            // set phone number text and listener
            phoneText.setText(placeDetails.result.formattedPhone);
            phoneText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        // dial phone number
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + placeDetails.result.phone));
                        startActivity(dialIntent);
                    } catch (ActivityNotFoundException e) {
                        CustomToast.makeText(getActivity(), R.string.place_details_cannot_call, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (placeDetails.result.photos == null) {

            // show no photos view
            noPhotosView.setVisibility(View.VISIBLE);
            photosViewPager.setVisibility(View.GONE);
            photosPagerIndicator.setVisibility(View.GONE);

        } else {

            // show photos pager
            noPhotosView.setVisibility(View.GONE);
            photosViewPager.setVisibility(View.VISIBLE);
            photosPagerIndicator.setVisibility(View.VISIBLE);

            // get all photo references
            List<String> photoReferences = new ArrayList<String>();
            for (PlaceDetailsResponse.Photo photo : placeDetails.result.photos) {
                photoReferences.add(photo.reference);
            }

            // set up photos adapter
            photosPagerAdapter = new PhotosPagerAdapter(getActivity(), photoReferences, screenWidth);
            photosViewPager.setAdapter(photosPagerAdapter);

            // set up photos pager indicator
            photosPagerIndicator.setViewPager(photosViewPager);
        }

        if (placeDetails.result.reviews == null || placeDetails.result.reviews.length == 0) {

            // hide reviews
            reviewsLayout.setVisibility(View.GONE);

        } else {

            // set up reviews adapter
            reviewsPagerAdapter = new ReviewsPagerAdapter(getActivity(), Arrays.asList(placeDetails.result.reviews));
            reviewsViewPager.setAdapter(reviewsPagerAdapter);

            // update arrows
            updateReviewsArrows();
        }

    }
}
