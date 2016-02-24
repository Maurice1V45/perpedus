package com.perpedus.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perpedus.android.R;
import com.perpedus.android.listener.SearchLanguageDialogListener;
import com.perpedus.android.util.LanguageUtils;

import java.util.List;

/**
 * Adapter for search language dialog
 */
public class SearchLanguageListAdapter extends RecyclerView.Adapter<SearchLanguageListAdapter.SearchLanguageViewHolder> {

    private List<String> languages;
    private Context context;
    private SearchLanguageDialogListener searchLanguageDialogListener;

    /**
     * Constructor
     *
     * @param context
     * @param languages
     * @param searchLanguageDialogListener
     */
    public SearchLanguageListAdapter(Context context, List<String> languages, SearchLanguageDialogListener searchLanguageDialogListener) {
        this.context = context;
        this.languages = languages;
        this.searchLanguageDialogListener = searchLanguageDialogListener;
    }

    @Override
    public SearchLanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate and return the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_language_list_item, parent, false);
        SearchLanguageViewHolder viewHolder = new SearchLanguageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchLanguageViewHolder holder, int position) {

        // get the place type
        final String language = languages.get(position);


        // set title
        holder.title.setText(context.getString(LanguageUtils.SUPPORTED_LANGUAGES_MAP.get(language)));

        // set on item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLanguageDialogListener.onLanguageSelected(language);
            }
        });

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    /**
     * View holder for languages
     */
    public static class SearchLanguageViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        View itemView;

        /**
         * Constructor
         *
         * @param itemView
         */
        SearchLanguageViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.itemView = itemView.findViewById(R.id.container);
        }

    }

}
