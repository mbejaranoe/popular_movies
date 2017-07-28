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

    private static final String TAG = MovieAdapter.class.getSimpleName();

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

        //int nameColumnIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        //Picasso.with(holder.moviePoster.getContext()).load(mCursor.getString(nameColumnIndex)).into(holder.moviePoster);
        //Picasso.with(holder.moviePoster.getContext()).load(mCursor.getString(MovieFragment.INDEX_MOVIE_POSTER_PATH)).into(holder.moviePoster);
        byte[] imageByteArray= mCursor.getBlob(MovieFragment.INDEX_MOVIE_POSTER_PATH);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.moviePoster.setImageBitmap(bitmap);
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
            int id = mCursor.getInt(mCursor.getColumnIndex(MovieContract.MovieEntry._ID));
            intent.putExtra("movie", id);
            view.getContext().startActivity(intent);

        }
    }
}
