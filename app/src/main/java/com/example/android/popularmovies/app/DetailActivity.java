package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.utilities.NetworkUtils;
import com.example.android.popularmovies.app.utilities.TMDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;

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

    public static ContentValues mMovieDetails;
    public static int mMarkedFavorite;
    public static FloatingActionButton mFab;


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

    public void onClickSetFavorite(View view) {

        if (mMarkedFavorite > 0) {
            mMarkedFavorite = 0;
        } else {
            mMarkedFavorite = 1;
        }

        mMovieDetails.put(MovieContract.MovieEntry.COLUMN_FAVORITE,mMarkedFavorite);
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

    public interface OnFetchMovieTrailerTaskCompleted {

        void OnFetchMovieTrailerTaskCompleted(ContentValues[] contentValues);

    }

    public static class DetailFragment extends Fragment implements OnFetchMovieTrailerTaskCompleted{

        private TrailerAdapter mTrailerAdapter;
        private RecyclerView mRecyclerview;

        public DetailFragment() {
        }

        @Override
        public void OnFetchMovieTrailerTaskCompleted(ContentValues[] contentValues) {
            mTrailerAdapter = new TrailerAdapter(contentValues);
        }

        public class FetchMovieTrailersAsyncTask extends AsyncTask<String, Void, ContentValues[]>{

            private OnFetchMovieTrailerTaskCompleted movieTrailerTasklistener;

            public FetchMovieTrailersAsyncTask(OnFetchMovieTrailerTaskCompleted listener){
                this.movieTrailerTasklistener = listener;
            }

            @Override
            protected ContentValues[] doInBackground(String... strings) {
                String id = strings[0];
                String trailersInfoJsonStr;

                /* Obtain String containing trailers info in Json */
                try {
                    trailersInfoJsonStr = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.getTrailersURL(id));
                } catch (IOException e){
                    Log.e("DetailFragment", "Error: ", e);
                    return null;
                }

                /* Obtain ContentValues[], each one contains trailer info from one item (trailer) */
                ContentValues[] trailers = null;
                try {
                    trailers = TMDBJsonUtils.getMovieTrailersFromJson(trailersInfoJsonStr);
                } catch (JSONException e) {
                    Log.e("DetailFragment", "Error: ", e);
                    return null;
                }

                return  trailers;
            }

            @Override
            protected void onPostExecute(ContentValues[] cv) {

                movieTrailerTasklistener.OnFetchMovieTrailerTaskCompleted(cv);

            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            mFab = (FloatingActionButton) rootView.findViewById(R.id.addFavorite_fab);
            mMovieDetails = new ContentValues();

            /* esto es añadido */
            mRecyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview_trailers);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerview.setLayoutManager(layoutManager);
            mRecyclerview.setHasFixedSize(true);
            mTrailerAdapter = new TrailerAdapter();
            mRecyclerview.setAdapter(mTrailerAdapter);
            /* esto es añadido*/

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
}