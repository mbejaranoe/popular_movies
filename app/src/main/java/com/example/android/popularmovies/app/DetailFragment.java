package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.app.data.MovieContract;

import static com.example.android.popularmovies.app.DetailActivity.DETAIL_MOVIE_PROJECTION;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_BACKDROP_IMAGE;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_FAVORITE;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_ID;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_MOVIE_POSTER;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_POPULARITY;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_RELEASE_DATE;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_SYNOPSIS;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_TITLE;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_TMDB_ID;
import static com.example.android.popularmovies.app.DetailActivity.INDEX_VOTE_AVERAGE;

/**
 * Created by Manolo on 04/08/2017.
 */

public class DetailFragment extends Fragment implements OnFetchMovieTrailerTaskCompleted{

    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mRecyclerview;

    public static ContentValues mMovieDetails;
    public static int mMarkedFavorite;
    public static FloatingActionButton mFab;

    public DetailFragment() {
    }

    @Override
    public void OnFetchMovieTrailerTaskCompleted(ContentValues[] contentValues) {

        mTrailerAdapter = new TrailerAdapter(contentValues);
        mRecyclerview.setAdapter(mTrailerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        mFab = (FloatingActionButton) rootView.findViewById(R.id.addFavorite_fab);
        mMovieDetails = new ContentValues();

        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview_trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setHasFixedSize(true);
        FetchMovieTrailersAsyncTask fetchMovieTrailersAsyncTask = new FetchMovieTrailersAsyncTask(this);

        if (intent != null && intent.hasExtra("movie")) {
            String tmdbId = intent.getStringExtra("movie");

            fetchMovieTrailersAsyncTask.execute(tmdbId);
            mRecyclerview.setAdapter(mTrailerAdapter);

            Cursor cursor;
            String[] projection = DETAIL_MOVIE_PROJECTION;
            String selection = MovieContract.MovieEntry.COLUMN_TMDB_ID + "=?";
            String[] selectionArgs = new String[]{tmdbId};
            String sortOrder = null;

            cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);

            cursor.moveToFirst();

            ImageView imgBackdrop = (ImageView) rootView.findViewById(R.id.backdrop_ImageView);
            byte[] imageByteArrayBackdrop= cursor.getBlob(INDEX_BACKDROP_IMAGE);
            Bitmap bitmapBackdrop = BitmapFactory.decodeByteArray(imageByteArrayBackdrop, 0, imageByteArrayBackdrop.length);
            imgBackdrop.setImageBitmap(bitmapBackdrop);

            ((TextView) rootView.findViewById(R.id.title_textView)).setText(cursor.getString(INDEX_TITLE));

            ((TextView) rootView.findViewById(R.id.date_textView)).setText(cursor.getString(INDEX_RELEASE_DATE));

            ImageView imgPoster = (ImageView) rootView.findViewById(R.id.poster_ImageView);
            byte[] imageByteArrayPoster= cursor.getBlob(INDEX_MOVIE_POSTER);
            Bitmap bitmapPoster = BitmapFactory.decodeByteArray(imageByteArrayPoster, 0, imageByteArrayPoster.length);
            imgPoster.setImageBitmap(bitmapPoster);

            ((TextView) rootView.findViewById(R.id.vote_Average_textView)).setText(String.valueOf(cursor.getLong(INDEX_VOTE_AVERAGE)));

            ((TextView) rootView.findViewById(R.id.synopsis_textView)).setText(cursor.getString(INDEX_SYNOPSIS));

            mMovieDetails.put(MovieContract.MovieEntry._ID,cursor.getInt(INDEX_ID));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_TITLE,cursor.getString(INDEX_TITLE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_TMDB_ID,cursor.getString(INDEX_TMDB_ID));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,cursor.getBlob(INDEX_MOVIE_POSTER));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE, cursor.getBlob(INDEX_BACKDROP_IMAGE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,cursor.getString(INDEX_SYNOPSIS));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,cursor.getString(INDEX_RELEASE_DATE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_POPULARITY,cursor.getLong(INDEX_POPULARITY));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,cursor.getLong(INDEX_VOTE_AVERAGE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE,cursor.getInt(INDEX_FAVORITE));
            mMarkedFavorite = cursor.getInt(INDEX_FAVORITE);

            if (mMarkedFavorite > 0) {
                mFab.setImageResource(R.drawable.ic_empty_heart);
            } else {
                mFab.setImageResource(R.drawable.ic_filled_heart);
            }
        }
        return rootView;
    }
}
