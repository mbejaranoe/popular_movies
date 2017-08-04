package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.app.data.MovieContract;

import static com.example.android.popularmovies.app.DetailFragment.mFab;
import static com.example.android.popularmovies.app.DetailFragment.mMarkedFavorite;
import static com.example.android.popularmovies.app.DetailFragment.mMovieDetails;

public class DetailActivity extends ActionBarActivity {

    public static final String[] DETAIL_MOVIE_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_TMDB_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_FAVORITE
        };

    public static final int INDEX_ID = 0;
    public static final int INDEX_TITLE = 1;
    public static final int INDEX_TMDB_ID = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_BACKDROP_IMAGE = 4;
    public static final int INDEX_SYNOPSIS = 5;
    public static final int INDEX_RELEASE_DATE = 6;
    public static final int INDEX_POPULARITY = 7;
    public static final int INDEX_VOTE_AVERAGE = 8;
    public static final int INDEX_FAVORITE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnFetchMovieTrailerTaskCompleted {

        void OnFetchMovieTrailerTaskCompleted(ContentValues[] contentValues);

    }

    public void onClickSetFavorite(View view) {

        if (mMarkedFavorite > 0) {
            mMarkedFavorite = 0;
        } else {
            mMarkedFavorite = 1;
        }

        mMovieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE, mMarkedFavorite);
        String selection = MovieContract.MovieEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mMovieDetails.getAsInteger(MovieContract.MovieEntry._ID))};
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;

        getContentResolver().update(uri,
                mMovieDetails,
                selection,
                selectionArgs
        );

        if (mMarkedFavorite > 0) {
            mFab.setImageResource(R.drawable.ic_empty_heart);
        } else {
            mFab.setImageResource(R.drawable.ic_filled_heart);
        }
    }

}