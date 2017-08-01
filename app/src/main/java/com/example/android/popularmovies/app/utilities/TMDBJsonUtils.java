package com.example.android.popularmovies.app.utilities;

import android.content.ContentValues;

import com.example.android.popularmovies.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Manolo on 24/07/2017.
 */

public final class TMDBJsonUtils {

    public static ContentValues[] getMovieInfoFromJson(String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_ID = "id";
        final String baseUrl = "http://image.tmdb.org/t/p/w185/";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_SYNOPSIS = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        JSONObject movieJson = new JSONObject(movieInfoJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(TMDB_RESULTS);
        ContentValues[] resultStr = new ContentValues[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            resultStr[i] = new ContentValues();
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_TITLE, resultsArray.getJSONObject(i).getString(TMDB_TITLE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_TMDB_ID, resultsArray.getJSONObject(i).getString(TMDB_ID));
            byte[] poster = NetworkUtils.getImageFromURL(baseUrl+resultsArray.getJSONObject(i).getString(TMDB_POSTER));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, poster);
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, resultsArray.getJSONObject(i).getString(TMDB_SYNOPSIS));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, resultsArray.getJSONObject(i).getString(TMDB_RELEASE_DATE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_POPULARITY, resultsArray.getJSONObject(i).getLong(TMDB_POPULARITY));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, resultsArray.getJSONObject(i).getLong(TMDB_VOTE_AVERAGE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
        }

        return resultStr;
    }

    public static String[] getMovieTrailersFromJson(String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_TYPE = "type";
        final String baseUrl = "https://www.youtube.com/watch?v=";
        JSONObject videos = new JSONObject(movieInfoJsonStr);
        JSONArray resultsArray = videos.getJSONArray(TMDB_RESULTS);
        int numTrailers = 0;

        for (int i = 0; i < resultsArray.length(); i++) {
            if (resultsArray.getJSONObject(i).get(TMDB_TYPE) == "Trailer"){
                numTrailers++;
            }
        }

        String[] trailers = new String[numTrailers];

        int trailersCount = 0;
        for (int i = 0; i < resultsArray.length(); i++){
            if (resultsArray.getJSONObject(i).get(TMDB_TYPE) == "Trailer"){
                trailers[trailersCount] = baseUrl + resultsArray.getJSONObject(i).getString(TMDB_KEY);
                trailersCount++;
            }
        }

        return trailers;
    }

    public static String[] getMovieReviewsFromJson(String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_REVIEW_URL = "url";
        final String TMDB_NUM_RESULTS = "total_results";

        int numReviews;

        JSONObject reviews = new JSONObject(movieInfoJsonStr);
        JSONArray resultsArray = reviews.getJSONArray(TMDB_RESULTS);
        numReviews = reviews.getInt(TMDB_NUM_RESULTS);
        String[] reviewsUrl = new String[numReviews];

        for (int i = 0; i < resultsArray.length(); i++){
            reviewsUrl[i] = resultsArray.getJSONObject(i).getString(TMDB_REVIEW_URL);
        }

        return reviewsUrl;
    }
}
