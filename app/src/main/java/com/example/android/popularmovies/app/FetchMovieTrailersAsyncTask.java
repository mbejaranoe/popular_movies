package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.app.utilities.NetworkUtils;
import com.example.android.popularmovies.app.utilities.TMDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Manolo on 04/08/2017.
 */

public class FetchMovieTrailersAsyncTask extends AsyncTask<String, Void, ContentValues[]> {

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