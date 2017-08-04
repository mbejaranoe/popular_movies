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
        final String TMDB_BACKDROP = "backdrop_path";
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
            byte[] backdrop = NetworkUtils.getImageFromURL(baseUrl+resultsArray.getJSONObject(i).getString(TMDB_BACKDROP));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE, backdrop);
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, resultsArray.getJSONObject(i).getString(TMDB_SYNOPSIS));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, resultsArray.getJSONObject(i).getString(TMDB_RELEASE_DATE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_POPULARITY, resultsArray.getJSONObject(i).getLong(TMDB_POPULARITY));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, resultsArray.getJSONObject(i).getLong(TMDB_VOTE_AVERAGE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
        }

        return resultStr;
    }

    public static ContentValues[] getMovieTrailersFromJson(String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_TYPE = "type";
        final String TMDB_NAME = "name";
        final String KEY_URL = "url";
        final String KEY_NAME = "name";
        final String baseUrl = "https://www.youtube.com/watch?v=";
        JSONObject videos = new JSONObject(movieInfoJsonStr);
        JSONArray resultsArray = videos.getJSONArray(TMDB_RESULTS);
        int numTrailers = 0;

        for (int i = 0; i < resultsArray.length(); i++) {
            String type = resultsArray.getJSONObject(i).get(TMDB_TYPE).toString();
            if (type.equals("Trailer")){
                numTrailers++;
            }
        }

        ContentValues[] trailers = new ContentValues[numTrailers];

        int trailersCount = 0;
        for (int i = 0; i < resultsArray.length(); i++){
            String type = resultsArray.getJSONObject(i).get(TMDB_TYPE).toString();
            if (type.equals("Trailer")){
                String url = String.valueOf(baseUrl + resultsArray.getJSONObject(i).getString(TMDB_KEY));
                String name = String.valueOf(resultsArray.getJSONObject(i).getString(TMDB_NAME));
                trailers[trailersCount] = new ContentValues();
                trailers[trailersCount].put(KEY_URL, url);
                trailers[trailersCount].put(KEY_NAME, name);
                trailersCount++;
            }
        }

        return trailers;
    }

    public static ContentValues[] getMovieReviewsFromJson(String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_REVIEW_URL = "url";
        final String TMDB_NUM_RESULTS = "total_results";
        final String TMDB_AUTHOR = "author";
        final String KEY_URL = "url";
        final String KEY_AUTHOR = "author";

        int numReviews;

        JSONObject reviewsJson = new JSONObject(movieInfoJsonStr);
        JSONArray resultsArray = reviewsJson.getJSONArray(TMDB_RESULTS);
        numReviews = reviewsJson.getInt(TMDB_NUM_RESULTS);
        ContentValues[] reviews = new ContentValues[numReviews];

        for (int i = 0; i < resultsArray.length(); i++){
            reviews[i] = new ContentValues();
            String url = resultsArray.getJSONObject(i).getString(TMDB_REVIEW_URL).toString();
            String author = resultsArray.getJSONObject(i).getString(TMDB_AUTHOR).toString();
            reviews[i].put(KEY_URL, url);
            reviews[i].put(KEY_AUTHOR, author);
        }

        return reviews;
    }
}
