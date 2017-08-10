package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Manolo on 08/08/2017.
 */

public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageButton playTrailerButton;
    private TextView trailerLabelTextView;
    private String trailerUrl;

    public TrailersViewHolder(View itemView) {
        super(itemView);

        playTrailerButton = (ImageButton) itemView.findViewById(R.id.play_trailer_button);
        trailerLabelTextView = (TextView) itemView.findViewById(R.id.trailer_label_textView);

        itemView.setOnClickListener(this);
    }

    public static void configureTrailersViewHolder(TrailersViewHolder trailersViewHolder, ContentValues[] trailers, int position) {
        trailersViewHolder.playTrailerButton.setImageResource(R.drawable.ic_play_button);
        trailersViewHolder.trailerLabelTextView.setText(trailers[position].getAsString("name"));
        trailersViewHolder.trailerUrl = trailers[position].getAsString("url");
    }

    @Override
    public void onClick(View view) {
        Uri youtubeUri = Uri.parse(trailerUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);

        if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
            view.getContext().startActivity(intent);
        }
    }
}
