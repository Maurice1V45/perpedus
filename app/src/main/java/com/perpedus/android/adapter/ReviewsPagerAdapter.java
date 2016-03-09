package com.perpedus.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.perpedus.android.R;
import com.perpedus.android.dom.PlaceDetailsResponse;
import com.perpedus.android.util.UrlUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Pager adapter for reviews
 */
public class ReviewsPagerAdapter extends PagerAdapter {

    private Context context;
    private List<PlaceDetailsResponse.Review> reviews;
    private LayoutInflater inflater;

    /**
     * Constructor
     *
     * @param context
     * @param reviews
     */
    public ReviewsPagerAdapter(Context context, List<PlaceDetailsResponse.Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View view = inflater.inflate(R.layout.reviews_pager_item, null);

        // get the review
        PlaceDetailsResponse.Review review = reviews.get(position);

        TextView userText = (TextView) view.findViewById(R.id.user_text);
        userText.setText(review.author);

        TextView commentText = (TextView) view.findViewById(R.id.comment_text);
        commentText.setText(review.comment);

        container.addView(view, 0);
        return view;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

}
