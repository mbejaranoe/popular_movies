package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Manolo on 08/08/2017.
 */

public class ReviewsViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView userTextView;
    private TextView reviewTextView;
    private String reviewString;
    private boolean shortReviewShown;

    public ReviewsViewholder(View itemView) {
        super(itemView);

        userTextView = (TextView) itemView.findViewById(R.id.user_textView);
        reviewTextView = (TextView) itemView.findViewById(R.id.review_textView);

        itemView.setOnClickListener(this);
    }

    public static void configureReviewsViewHolder(ReviewsViewholder reviewsViewholder, ContentValues[] reviews, int position){
        reviewsViewholder.userTextView.setText(reviews[position].getAsString("author"));
        reviewsViewholder.reviewString = reviews[position].getAsString("review");
        if (reviewsViewholder.reviewString.length()>100) {
            reviewsViewholder.reviewTextView.setText(reviewsViewholder.reviewString.substring(0,101) + "...(more)");
            reviewsViewholder.shortReviewShown = true;
        } else {
            reviewsViewholder.reviewTextView.setText(reviewsViewholder.reviewString);
            reviewsViewholder.shortReviewShown = false;
        }
    }

    @Override
    public void onClick(View view) {
        if (shortReviewShown) {
            reviewTextView.setText(reviewString);
            shortReviewShown = false;
        } else {
            reviewTextView.setText(reviewString.substring(0,101) + "...(more)");
            shortReviewShown = true;
        }
    }
}
