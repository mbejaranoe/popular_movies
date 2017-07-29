package com.example.android.popularmovies.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.app.data.MovieContract;

public class DetailActivity extends ActionBarActivity {

    public static final String[] DETAIL_MOVIE_PROJECTION = {MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_TMDB_ID
        };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_RELEASE_DATE = 1;
    public static final int INDEX_MOVIE_POSTER = 2;
    public static final int INDEX_VOTE_AVERAGE = 3;
    public static final int INDEX_SYNOPSIS = 4;
    public static final int INDEX_TMDB_ID = 5;

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

    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movie")) {
                String tmdbId = intent.getStringExtra("movie");
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
                ((TextView) rootView.findViewById(R.id.title_textView)).setText("Title: " + cursor.getString(INDEX_TITLE));
                ((TextView) rootView.findViewById(R.id.date_textView)).setText("Release date: " + cursor.getString(INDEX_RELEASE_DATE));
                ImageView img = (ImageView) rootView.findViewById(R.id.poster_ImageView);
                byte[] imageByteArray= cursor.getBlob(INDEX_MOVIE_POSTER);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                img.setImageBitmap(bitmap);
                ((TextView) rootView.findViewById(R.id.vote_Average_textView)).setText("Vote average: " + String.valueOf(cursor.getLong(INDEX_VOTE_AVERAGE)));
                ((TextView) rootView.findViewById(R.id.synopsis_textView)).setText(cursor.getString(INDEX_SYNOPSIS));
            }
            return rootView;
        }
    }
}