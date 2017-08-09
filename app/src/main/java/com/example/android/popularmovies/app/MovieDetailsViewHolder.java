package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.app.data.MovieContract;

/**
 * Created by Manolo on 08/08/2017.
 */

public class MovieDetailsViewHolder extends RecyclerView.ViewHolder {

    private ImageView backdropdImageView;
    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView voteAverageTextView;
    private ImageButton addFavoriteButton;
    private TextView synopsisTextView;

    public MovieDetailsViewHolder(View itemView) {
        super(itemView);

        backdropdImageView = (ImageView) itemView.findViewById(R.id.backdrop_ImageView);
        posterImageView = (ImageView) itemView.findViewById(R.id.poster_ImageView);
        titleTextView = (TextView) itemView.findViewById(R.id.title_textView);
        dateTextView = (TextView) itemView.findViewById(R.id.date_textView);
        voteAverageTextView = (TextView) itemView.findViewById(R.id.vote_Average_textView);
        addFavoriteButton = (ImageButton) itemView.findViewById(R.id.addFavorite_button);
        synopsisTextView = (TextView) itemView.findViewById(R.id.synopsis_textView);
    }

    public static void configureMovieDetailsViewHolder(MovieDetailsViewHolder holder, ContentValues contentValues){

        byte[] imageByteArrayBackdrop = contentValues.getAsByteArray(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE);
        Bitmap bitmapBackdrop = BitmapFactory.decodeByteArray(imageByteArrayBackdrop, 0, imageByteArrayBackdrop.length);
        holder.backdropdImageView.setImageBitmap(bitmapBackdrop);

        byte[] imageByteArrayPoster = contentValues.getAsByteArray(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        Bitmap bitmapPoster = BitmapFactory.decodeByteArray(imageByteArrayPoster, 0, imageByteArrayPoster.length);
        holder.posterImageView.setImageBitmap(bitmapPoster);

        holder.titleTextView.setText(contentValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE));

        holder.dateTextView.setText(contentValues.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));

        holder.voteAverageTextView.setText(String.valueOf(contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));

        int favorite = contentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_FAVORITE);
        if (favorite > 0) {
            holder.addFavoriteButton.setImageResource(R.drawable.ic_filled_heart);
        } else {
            holder.addFavoriteButton.setImageResource(R.drawable.ic_empty_heart);
        }

        holder.synopsisTextView.setText(contentValues.getAsString(MovieContract.MovieEntry.COLUMN_SYNOPSIS));
    }
}
