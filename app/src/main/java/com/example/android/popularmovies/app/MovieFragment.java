package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.utilities.NetworkUtils;
import com.example.android.popularmovies.app.utilities.TMDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;
    private String mSortOrder;

    private static final int MOVIE_LOADER_ID = 0;

    public static final String[] FRAGMENT_MOVIE_PROJECTION = {MovieContract.MovieEntry.COLUMN_TMDB_ID, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER};

    public static final int INDEX_MOVIE_TMDB_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;

    public MovieFragment(){
    }

    public class fetchMovieDataAsyncTask extends AsyncTask<Void, Void, ContentValues[]> {

        @Override
        protected ContentValues[] doInBackground(Void... voids) {
            String movieInfoJsonStr;

            /* Obtain String containing movie info in Json */
            try {
                movieInfoJsonStr = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.getURL(getContext()));
            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
                return null;
            }

            /* Obtain ContentValues[], each one contains movie info from one item (movie) */
            ContentValues[] result = null;
            try {
                result = TMDBJsonUtils.getMovieInfoFromJson(movieInfoJsonStr);
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
        mSortOrder = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.pref_order_key),getString(R.string.pref_order_default));
        if (!mSortOrder.equals(getString(R.string.pref_order_favorite))) {
            final ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                new fetchMovieDataAsyncTask().execute();
            } else {
                // notify user you are not online
                Toast.makeText(getActivity(), "You are not online!", Toast.LENGTH_LONG).show();
            }
        }
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
    public void onResume(){
        super.onResume();
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.pref_order_key),getString(R.string.pref_order_default));
        if (!mSortOrder.equals(sortOrder)) {
            mSortOrder=sortOrder;
            LoaderManager loaderManager = this.getLoaderManager();
            loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
        }
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