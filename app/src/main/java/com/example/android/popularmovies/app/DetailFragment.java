package com.example.android.popularmovies.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class DetailFragment extends Fragment implements OnFetchMovieTrailerTaskCompleted, OnFetchMovieReviewTaskCompleted {

    private DetailsAdapter mDetailsAdapter;
    private RecyclerView mRecyclerview;

    public static ContentValues mMovieDetails;
    public static int mMarkedFavorite;
    public static String mTrailerUrl;
    public static ContentValues[] mTrailers;
    public static ContentValues[] mReviews;

    public static final String[] TRAILERS_PROJECTION = {
            MovieContract.TrailersEntry._ID,
            MovieContract.TrailersEntry.COLUMN_TMDB_ID,
            MovieContract.TrailersEntry.COLUMN_URL,
            MovieContract.TrailersEntry.COLUMN_NAME
    };

    public static final int TRAILERS_INDEX_ID = 0;
    public static final int TRAILERS_INDEX_TMDB_ID = 1;
    public static final int TRAILERS_INDEX_URL = 2;
    public static final int TRAILERS_INDEX_NAME = 3;

    public static final String[] REVIEWS_PROJECTION = {
            MovieContract.ReviewsEntry._ID,
            MovieContract.ReviewsEntry.COLUMN_TMDB_ID,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_REVIEW
    };

    public static final int REVIEWS_INDEX_ID = 0;
    public static final int REVIEWS_INDEX_TMDB_ID = 1;
    public static final int REVIEWS_INDEX_AUTHOR = 2;
    public static final int REVIEWS_INDEX_REVIEW = 3;


    public DetailFragment() {
    }

    @Override
    public void OnFetchMovieTrailerTaskCompleted(ContentValues[] contentValues) {

        ContentValues[] cvToInsert = contentValues.clone();

        for (int i = 0; i < contentValues.length; i++) {
            cvToInsert[i].put("tmdbId", mMovieDetails.getAsString(MovieContract.TrailersEntry.COLUMN_TMDB_ID));
        }

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = MovieContract.TrailersEntry.CONTENT_URI;
        contentResolver.bulkInsert(uri,cvToInsert);

        mDetailsAdapter.setTrailers(contentValues);
        mRecyclerview.setAdapter(mDetailsAdapter);
        mTrailerUrl = contentValues[0].getAsString("url");
    }

    @Override
    public void OnFetchMovieReviewTaskCompleted(ContentValues[] contentValues) {

        ContentValues[] cvToInsert = contentValues.clone();

        for (int i = 0; i < contentValues.length; i++) {
            cvToInsert[i].put("tmdbId", mMovieDetails.getAsString(MovieContract.ReviewsEntry.COLUMN_TMDB_ID));
        }

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = MovieContract.ReviewsEntry.CONTENT_URI;
        contentResolver.bulkInsert(uri,cvToInsert);

        mDetailsAdapter.setReviews(contentValues);
        mRecyclerview.setAdapter(mDetailsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        mMovieDetails = new ContentValues();
        mDetailsAdapter = new DetailsAdapter();

        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview_details);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setHasFixedSize(true);
        FetchMovieTrailersAsyncTask fetchMovieTrailersAsyncTask = new FetchMovieTrailersAsyncTask(this);
        FetchMovieReviewsAsyncTask fetchMovieReviewsAsyncTask = new FetchMovieReviewsAsyncTask(this);

        if (intent != null && intent.hasExtra("movie")) {
            String tmdbId = intent.getStringExtra("movie");

            Cursor cursorMovie;
            String[] projection = DETAIL_MOVIE_PROJECTION;
            String selection = MovieContract.MovieEntry.COLUMN_TMDB_ID + "=?";
            String[] selectionArgs = new String[]{tmdbId};
            String sortOrder = null;

            cursorMovie = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);

            cursorMovie.moveToFirst();

            mMovieDetails.put(MovieContract.MovieEntry._ID,cursorMovie.getInt(INDEX_ID));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_TITLE,cursorMovie.getString(INDEX_TITLE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_TMDB_ID,cursorMovie.getString(INDEX_TMDB_ID));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,cursorMovie.getBlob(INDEX_MOVIE_POSTER));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE, cursorMovie.getBlob(INDEX_BACKDROP_IMAGE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,cursorMovie.getString(INDEX_SYNOPSIS));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,cursorMovie.getString(INDEX_RELEASE_DATE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_POPULARITY,cursorMovie.getLong(INDEX_POPULARITY));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,cursorMovie.getLong(INDEX_VOTE_AVERAGE));
            mMovieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE,cursorMovie.getInt(INDEX_FAVORITE));
            mDetailsAdapter.setMovieDetails(mMovieDetails);

            mMarkedFavorite = cursorMovie.getInt(INDEX_FAVORITE);
            cursorMovie.close();

            projection = TRAILERS_PROJECTION;
            selection = MovieContract.TrailersEntry.COLUMN_TMDB_ID + "=?";
            Cursor cursorTrailers;

            cursorTrailers = getContext().getContentResolver().query(MovieContract.TrailersEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);

            if (cursorTrailers != null && cursorTrailers.getCount() > 0) {
                mTrailers = new ContentValues[cursorTrailers.getCount()];
                cursorTrailers.moveToFirst();
                int i = 0;
                while (cursorTrailers.isAfterLast() == false) {
                    mTrailers[i] = new ContentValues();
                    mTrailers[i].put(MovieContract.TrailersEntry._ID, cursorTrailers.getInt(TRAILERS_INDEX_ID));
                    mTrailers[i].put(MovieContract.TrailersEntry.COLUMN_TMDB_ID, cursorTrailers.getString(TRAILERS_INDEX_TMDB_ID));
                    mTrailers[i].put(MovieContract.TrailersEntry.COLUMN_URL, cursorTrailers.getString(TRAILERS_INDEX_URL));
                    mTrailers[i].put(MovieContract.TrailersEntry.COLUMN_NAME, cursorTrailers.getString(TRAILERS_INDEX_NAME));
                    i++;
                    cursorTrailers.moveToNext();
                }
                mDetailsAdapter.setTrailers(mTrailers);
                mRecyclerview.setAdapter(mDetailsAdapter);
                mTrailerUrl = mTrailers[0].getAsString("url");
            } else {
                ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    fetchMovieTrailersAsyncTask.execute(tmdbId);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.offline_status_trailers_message), Toast.LENGTH_LONG).show();
                }
            }

            cursorTrailers.close();

            projection = REVIEWS_PROJECTION;
            selection = MovieContract.ReviewsEntry.COLUMN_TMDB_ID + "=?";
            Cursor cursorReviews;

            cursorReviews = getContext().getContentResolver().query(MovieContract.ReviewsEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);

            if (cursorReviews != null && cursorReviews.getCount() > 0) {
                mReviews = new ContentValues[cursorReviews.getCount()];
                cursorReviews.moveToFirst();
                int i = 0;
                while (cursorReviews.isAfterLast() == false) {
                    mReviews[i] = new ContentValues();
                    mReviews[i].put(MovieContract.ReviewsEntry._ID, cursorReviews.getInt(REVIEWS_INDEX_ID));
                    mReviews[i].put(MovieContract.ReviewsEntry.COLUMN_TMDB_ID, cursorReviews.getString(REVIEWS_INDEX_TMDB_ID));
                    mReviews[i].put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, cursorReviews.getString(REVIEWS_INDEX_AUTHOR));
                    mReviews[i].put(MovieContract.ReviewsEntry.COLUMN_REVIEW, cursorReviews.getString(REVIEWS_INDEX_REVIEW));
                    i++;
                    cursorReviews.moveToNext();
                }
                mDetailsAdapter.setReviews(mReviews);
                mRecyclerview.setAdapter(mDetailsAdapter);
            } else {
                ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    fetchMovieReviewsAsyncTask.execute(tmdbId);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.offline_status_reviews_message), Toast.LENGTH_LONG).show();
                }
            }

            mRecyclerview.setAdapter(mDetailsAdapter);
            cursorReviews.close();

        }
        return rootView;
    }
}
