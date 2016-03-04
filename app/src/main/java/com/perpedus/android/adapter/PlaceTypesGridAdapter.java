package com.perpedus.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.perpedus.android.R;
import com.perpedus.android.listener.PlaceTypesDialogListener;
import com.perpedus.android.util.PlacesHelper;

import java.util.List;

/**
 * Adapter for place types dialog
 */
public class PlaceTypesGridAdapter extends RecyclerView.Adapter<PlaceTypesGridAdapter.PlaceTypesViewHolder> {

    private List<String> placeTypes;
    private Context context;
    private PlaceTypesDialogListener placeTypesDialogListener;

    /**
     * Constructor
     *
     * @param context
     * @param placeTypesDialogListener
     */
    public PlaceTypesGridAdapter(Context context, List<String> placeTypes, PlaceTypesDialogListener placeTypesDialogListener) {
        this.context = context;
        this.placeTypes = placeTypes;
        this.placeTypesDialogListener = placeTypesDialogListener;
    }

    @Override
    public PlaceTypesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate and return the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_types_grid_item, parent, false);
        PlaceTypesViewHolder viewHolder = new PlaceTypesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlaceTypesViewHolder holder, int position) {

        // get the place type
        final String placeType = placeTypes.get(position);

        // set icon
        holder.icon.setImageResource(PlacesHelper.getInstance().getPlaceIcon(placeType));

        // set title
        holder.title.setText(PlacesHelper.getInstance().getPlaceTypeName(placeType));

        // set on item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                placeTypesDialogListener.onPlaceTypeSelected(placeType);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placeTypes.size();
    }

    /**
     * View holder for place types
     */
    public static class PlaceTypesViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        View itemView;

        /**
         * Constructor
         *
         * @param itemView
         */
        PlaceTypesViewHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.itemView = itemView;
        }

    }

}
