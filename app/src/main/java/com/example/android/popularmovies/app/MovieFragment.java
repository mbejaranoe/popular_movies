package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.utilities.NetworkUtils;

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

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
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
                ContentValues[] contentValues = NetworkUtils.fetchMovieData(getContext());
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,contentValues);

                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";

                return new CursorLoader(getContext(),
                        movieQueryUri,
                        FRAGMENT_MOVIE_PROJECTION,
                        null,
                        null,
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

    /*
    private Cursor getMovieInfo(){
        return mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_POPULARITY
        );
    }

    private long addMovies(ContentValues[] contentValues){
        int rowsInserted = 0;

        if (contentValues.length == 0 || contentValues == null) {
            return 0;
        } else {
            for (int i = 0; i < contentValues.length ; i++) {
                contentValues[i].put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
                rowsInserted = rowsInserted + mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues[i]);
            }
        }
        return rowsInserted;
    }
    */

}