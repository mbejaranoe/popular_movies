package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.app.data.MovieContract;

/**
 * Created by Manolo on 18/07/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Cursor mCursor;

    public MovieAdapter (){
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.grid_item_movie;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        byte[] imageByteArrayPoster= mCursor.getBlob(MovieFragment.INDEX_MOVIE_POSTER_PATH);
        Bitmap bitmapPoster = BitmapFactory.decodeByteArray(imageByteArrayPoster, 0, imageByteArrayPoster.length);
        holder.moviePoster.setImageBitmap(bitmapPoster);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView moviePoster;

        public MovieAdapterViewHolder(View view){
            super(view);

            moviePoster = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String tmdbId = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID));
            intent.putExtra("movie", tmdbId);
            view.getContext().startActivity(intent);
        }
    }
}
