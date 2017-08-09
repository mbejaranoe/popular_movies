package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Manolo on 08/08/2017.
 */

public class ReviewsViewholder extends RecyclerView.ViewHolder {

    private TextView userTextView;
    private TextView reviewTextView;

    public ReviewsViewholder(View itemView) {
        super(itemView);

        userTextView = (TextView) itemView.findViewById(R.id.user_textView);
        reviewTextView = (TextView) itemView.findViewById(R.id.review_textView);
    }

    public static void configureReviewsViewHolder(ReviewsViewholder reviewsViewholder, ContentValues[] reviews, int position){
        reviewsViewholder.userTextView.setText(reviews[position].getAsString("author"));
        reviewsViewholder.reviewTextView.setText(reviews[position].getAsString("review"));
    }

}
