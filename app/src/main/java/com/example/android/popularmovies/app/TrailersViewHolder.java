package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Manolo on 08/08/2017.
 */

public class TrailersViewHolder extends RecyclerView.ViewHolder {

    private ImageButton playTrailerButton;
    private TextView trailerLabelTextView;

    public TrailersViewHolder(View itemView) {
        super(itemView);

        playTrailerButton = (ImageButton) itemView.findViewById(R.id.play_trailer_button);
        trailerLabelTextView = (TextView) itemView.findViewById(R.id.trailer_label_textView);
    }

    public static void configureTrailersViewHolder(TrailersViewHolder trailersViewHolder, ContentValues[] trailers, int position) {
        trailersViewHolder.playTrailerButton.setImageResource(R.drawable.ic_play_button);
        trailersViewHolder.trailerLabelTextView.setText(trailers[position].getAsString("name"));
    }
}
