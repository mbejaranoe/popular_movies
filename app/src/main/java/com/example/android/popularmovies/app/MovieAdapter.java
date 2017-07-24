package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Manolo on 18/07/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieList;

    public MovieAdapter (ArrayList<Movie> movieList){
        mMovieList=movieList;
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
        Movie itemMovie = mMovieList.get(position);
        holder.bind(itemMovie);
}

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView moviePoster;
        private Movie mMovie;

        public MovieAdapterViewHolder(View view){
            super(view);

            moviePoster = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);

            view.setOnClickListener(this);
        }

        void bind(Movie movie){
            mMovie = movie;
            Picasso.with(moviePoster.getContext()).load(movie.posterPath).into(moviePoster);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra("movie", mMovie);
            view.getContext().startActivity(intent);

        }
    }
}
