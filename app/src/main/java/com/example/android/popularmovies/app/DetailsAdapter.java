package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Manolo on 08/08/2017.
 */

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int MOVIE_DETAILS = 0;
    private final int TRAILERS = 1;
    private final int REVIEWS = 2;

    private ContentValues movieDetails;
    private ContentValues[] trailersArray;
    private ContentValues[] reviewsArray;

    public void setMovieDetails(ContentValues movieDetails) {
        this.movieDetails = movieDetails;
    }

    public void setTrailers(ContentValues[] trailers){
        this.trailersArray = trailers;
    }

    public void setReviews(ContentValues[] reviews){
        this.reviewsArray = reviews;
    }

    @Override
    public int getItemCount() {
        return ((movieDetails == null) ? 0 : 1 ) + ((trailersArray == null) ? 0 : 1) + ((reviewsArray == null) ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return MOVIE_DETAILS;
        } else if (position == 1) {
            return TRAILERS;
        } else if (position == 2) {
            return REVIEWS;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case MOVIE_DETAILS:
                View movieDetailsView = inflater.inflate(R.layout.movie_details_viewholder, viewGroup, false);
                viewHolder = new MovieDetailsViewHolder(movieDetailsView);
                break;
            case TRAILERS:
                View trailersView = inflater.inflate(R.layout.trailers_viewholder, viewGroup, false);
                viewHolder = new TrailersViewHolder(trailersView);
                break;
            case REVIEWS:
                View reviewsView = inflater.inflate(R.layout.reviews_viewholder, viewGroup, false);
                viewHolder = new ReviewsViewholder(reviewsView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()){
            case MOVIE_DETAILS:
                MovieDetailsViewHolder movieDetailsViewHolder = (MovieDetailsViewHolder) viewHolder;
                MovieDetailsViewHolder.configureMovieDetailsViewHolder(movieDetailsViewHolder, movieDetails);
                break;
            case TRAILERS:
                TrailersViewHolder trailersViewHolder = (TrailersViewHolder) viewHolder;
                TrailersViewHolder.configureTrailersViewHolder(trailersViewHolder, trailersArray, position - 1);
                break;
            case REVIEWS:
                ReviewsViewholder reviewsViewholder = (ReviewsViewholder) viewHolder;
                ReviewsViewholder.configureReviewsViewHolder(reviewsViewholder, reviewsArray, position - 1 - trailersArray.length);
                break;
        }
    }
}
