package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Manolo on 04/08/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    ContentValues[] reviews;

    public ReviewAdapter(ContentValues[] contentValues){
        reviews = contentValues;
    }

    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.list_item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        ReviewAdapterViewHolder reviewAdapterViewHolder = new ReviewAdapterViewHolder(view);

        return reviewAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        ContentValues review = reviews[position];

        holder.authorTextView.setText(review.getAsString("author"));
        /*
        holder.reviewTextView.setText(review.getAsString("url"));
        */
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView authorTextView;
        /*
        TextView reviewTextView;
        */

        public ReviewAdapterViewHolder(View itemView){
            super(itemView);

            authorTextView = (TextView) itemView.findViewById(R.id.user_textView);
            /*
            reviewTextView = (TextView) itemView.findViewById(R.id.review_textView);
            */

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // use an Intent to launch the review in a web browser
        }
    }
}
