package com.perpedus.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.perpedus.android.R;
import com.perpedus.android.util.UrlUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Pager adapter for photos
 */
public class PhotosPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> references;
    private int screenWidth;
    private LayoutInflater inflater;

    /**
     * Constructor
     *
     * @param context
     * @param references
     */
    public PhotosPagerAdapter(Context context, List<String> references, int screenWidth) {
        this.context = context;
        this.references = references;
        this.screenWidth = screenWidth;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return references.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View view = inflater.inflate(R.layout.photos_pager_item, null);

        // get the reference
        String reference = references.get(position);

        ImageView photoView = (ImageView) view.findViewById(R.id.photo);
        Picasso.with(context).load(UrlUtils.buildPlacePhotoLink(reference, screenWidth)).placeholder(R.drawable.progress).into(photoView);

        container.addView(view, 0);
        return view;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

}
