package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;

    private static final int MOVIE_LOADER_ID = 0;

    public static final String[] FRAGMENT_MOVIE_PROJECTION = {MovieContract.MovieEntry.COLUMN_MOVIE_POSTER};

    public static final int INDEX_MOVIE_POSTER_PATH = 0;

    public MovieFragment(){
    }

    public class fetchMovieDataAsyncTask extends AsyncTask<Void, Void, ContentValues[]> {

        @Override
        protected ContentValues[] doInBackground(Void... voids) {
            String movieInfoJsonStr;

            try {
                movieInfoJsonStr = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.getURL(getContext()));
            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
                return null;
            }
            ContentValues[] result = null;
            try {
                result = NetworkUtils.getMovieInfoFromJson(movieInfoJsonStr);
            } catch (JSONException e) {
                Log.e("NetworkUtils", e.getMessage(), e);
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ContentValues[] cv) {
            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,cv);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        new fetchMovieDataAsyncTask().execute();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movie);
        mGridLayoutManager = new GridLayoutManager(getContext(), 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);

        return rootView;
    }

    private void updateMovies() {
        int loaderId = MOVIE_LOADER_ID;

        LoaderManager loaderManager = this.getLoaderManager();
        LoaderManager.LoaderCallbacks<Cursor> callback = this;
        loaderManager.initLoader(loaderId, null, callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId){
            case MOVIE_LOADER_ID:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String pref_order = sharedPreferences.getString(getString(R.string.pref_order_key), getString(R.string.pref_order_default));

                String selection;
                String[] selectionArgs;
                if (pref_order.equals(getString(R.string.pref_order_favorite))){
                    selection = MovieContract.MovieEntry.COLUMN_FAVORITE + "=?";
                    selectionArgs = new String[]{"1"};
                } else {
                    selection = null;
                    selectionArgs = null;
                }

                String sortOrder;
                if (pref_order.equals(getString(R.string.pref_order_rated))){
                    sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                } else {
                    sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                }

                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                return new CursorLoader(getContext(),
                        movieQueryUri,
                        FRAGMENT_MOVIE_PROJECTION,
                        selection,
                        selectionArgs,
                        sortOrder);
            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}