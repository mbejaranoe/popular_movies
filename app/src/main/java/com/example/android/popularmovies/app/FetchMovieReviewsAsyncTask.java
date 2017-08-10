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

public class FetchMovieReviewsAsyncTask extends AsyncTask<String, Void, ContentValues[]> {

    private OnFetchMovieReviewTaskCompleted movieReviewTasklistener;

    public FetchMovieReviewsAsyncTask(OnFetchMovieReviewTaskCompleted listener){
        this.movieReviewTasklistener = listener;
    }

    @Override
    protected ContentValues[] doInBackground(String... strings) {
        String id = strings[0];
        String reviewsInfoJsonStr;

        /* Obtain String containing reviews info in Json */
        try {
            reviewsInfoJsonStr = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.getReviewsURL(id));
        } catch (IOException e){
            Log.e("DetailFragment", "Error: ", e);
            return null;
        }

        /* Obtain ContentValues[], each one contains review info from one item (review) */
        ContentValues[] reviews = null;
        try {
            reviews = TMDBJsonUtils.getMovieReviewsFromJson(reviewsInfoJsonStr);
        } catch (JSONException e) {
            Log.e("DetailFragment", "Error: ", e);
            return null;
        }

        return reviews;
    }

    @Override
    protected void onPostExecute(ContentValues[] cv) {

        movieReviewTasklistener.OnFetchMovieReviewTaskCompleted(cv);

    }
}
