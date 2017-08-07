package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Manolo on 03/08/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ContentValues[] trailers;

    public TrailerAdapter(ContentValues[] contentValues) {
        trailers = contentValues;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.list_item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        TrailerAdapterViewHolder trailerAdapterViewHolder = new TrailerAdapterViewHolder(view);

        return trailerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder holder, int position) {
        ContentValues trailer = trailers[position];

        holder.trailerLabelTextView.setText(trailer.getAsString("name"));
    }

    @Override
    public int getItemCount() {
        return trailers.length;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton playTrailerButton;
        TextView trailerLabelTextView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);

            playTrailerButton = (ImageButton) itemView.findViewById(R.id.play_trailer_button);
            trailerLabelTextView = (TextView) itemView.findViewById(R.id.trailer_label_textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String url = trailers[adapterPosition].get("url").toString();
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            intent.putExtra("url", url);

            if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                view.getContext().startActivity(intent);
            }
        }
    }
}